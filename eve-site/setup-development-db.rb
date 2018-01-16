#!/usr/bin/env ruby

require 'resolv'
require 'mysql2'
require 'java-properties'
require 'pp'

CONFIG_FILES = [
  'src/main/resources/persistence.properties',
  'src/main/resources/static.properties',
  'src/main/resources/local.properties',
]

config = CONFIG_FILES
  .map { |filename| File.expand_path(filename) }
  .select { |filename| File.exists?(filename) }
  .map { |filename| JavaProperties.load(filename) }
  .reduce(&:merge)

pp config

host_and_port = config[:'dataSource.url'].split("jdbc:mysql://")[1].split("/", 2)

# This is nonsens is needed because if you pass "localhost" to the mysql client it assumes it can connect with a socker. The mysql2 gem
# also doesn't expose the MYSQL_OPT_PROTOCOL flag which could otherwise be used to force it to TCP. So instead we lookup the mysql host
# on IPv4 (rip v6 hosts) and use that IP as the connect host
HOST = Resolv::DNS.open do |dns|
  res = dns.getresource(host_and_port[0], Resolv::DNS::Resource::IN::A).address
end
DATABASE = host_and_port[1]
USER = config[:'dataSource.username']
PASSWORD = config[:'dataSource.password']

if !ENV['CREATE_USERS'].nil?
  super_user = ENV['MYSQL_USER']

  puts "Creating uses, and databases, on host #{HOST} with the user #{super_user}"

  super_client = Mysql2::Client.new(
    host:  HOST, 
    username: super_user, 
    password: ENV['MYSQL_PASSWORD'], 
    database: 'mysql')

  super_client.query("create database if not exists #{DATABASE}")
  super_client.query("create database if not exists `eve-dump`")
  super_client.query("GRANT ALL PRIVILEGES ON #{DATABASE}.* TO '#{USER}'@'%' IDENTIFIED BY '#{PASSWORD}'")
  super_client.query("GRANT ALL PRIVILEGES ON `eve-dump`.* TO '#{USER}'@'%' IDENTIFIED BY '#{PASSWORD}'")
  super_client.query("GRANT SUPER ON *.* TO '#{USER}'@'%' IDENTIFIED BY '#{PASSWORD}'")
  super_client.query("SET GLOBAL sql_mode=(SELECT REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY',''));")
end

# dump_file = File.expand_path('src/main/sql/mysql-latest.tar.bz2')
# expanded_dump = File.expand_path('src/main/sql/export')
# dump_files = Dir["#{expanded_dump}/**/*"].select { |filename| File.file? filename }

# if dump_files.count > 1
#   puts "Found more than one export. Please ensure only one is present or set DUMP_OVERWRITE"
#   exit 1
# elsif dump_files.count == 1
#   puts "Found existing dump at #{dump_files[0]}. Importing it."
# end

# if !File.exists?(expanded_dump)
#   puts "Expanded not found at #{expanded_dump}, unpacking it from #{dump_file}"
#   puts `mkdir #{expanded_dump} && tar -jxvf #{dump_file} -C #{expanded_dump}`
# end

def drop_and_create(database)
  begin
    client = Mysql2::Client.new(
      host:  HOST, 
      username: USER, 
      password: PASSWORD, 
      database: database)
  rescue Mysql2::Error
    puts "Did you remember to run this the first time with the CREATE_USERS env option?"
    raise
  end

  if !ENV['DROP_DATABASE'] && client.query("show tables").count > 0
    puts "#{database} already loaded and not force reloading it."
  else
    if ENV['DROP_DATABASE']
      puts "Recreating database #{database}"
      client.query("drop database if exists `#{database}`")
      client.query("create database if not exists `#{database}`")
      client.query("use `#{database}`")
    end

    yield client if block_given?
  end

  client.close
end

def split_statements(filename, &block)
  File.read(filename).split(";\n").each(&block)
end

def run_statements(client, filename)
  split_statements(filename) { |statement| client.query(statement) }
end

def import_file(filename, database)
  puts `mysql -h #{HOST} -u #{USER} --password=#{PASSWORD} #{database} < #{filename}`
end

drop_and_create 'eve-dump' do |client|
  # eve_load_file = Dir["#{expanded_dump}/**/*"].select { |filename| File.file? filename }[0]
  import_file(File.expand_path('src/main/sql/eve-dump-selective.sql'), 'eve-dump')
  import_file(File.expand_path('src/main/sql/add-eve-dump-indexes.sql'), 'eve-dump')
end

drop_and_create DATABASE do |client|
  import_file(File.expand_path('src/main/sql/create-schema.sql'), DATABASE)
  import_file(File.expand_path('src/main/sql/insert-data.sql'), DATABASE)
end