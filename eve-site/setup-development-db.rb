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
host = Resolv::DNS.open do |dns|
  res = dns.getresource(host_and_port[0], Resolv::DNS::Resource::IN::A).address
end
database = host_and_port[1]
user = config[:'dataSource.username']
password = config[:'dataSource.password']

if !ENV['CREATE_USERS'].nil?
  super_user = ENV['MYSQL_USER']

  puts "Creating uses, and databases, on host #{host} with the user #{super_user}"

  super_client = Mysql2::Client.new(
    host:  host, 
    username: super_user, 
    password: ENV['MYSQL_PASSWORD'], 
    database: 'mysql')

  super_client.query("create database if not exists #{database}")
  super_client.query("create database if not exists `eve-dump`")
  super_client.query("GRANT ALL PRIVILEGES ON #{database}.* TO '#{user}'@'%' IDENTIFIED BY '#{password}'")
  super_client.query("GRANT ALL PRIVILEGES ON `eve-dump`.* TO '#{user}'@'%' IDENTIFIED BY '#{password}'")
end

begin
  client = Mysql2::Client.new(
    host:  host, 
    username: user, 
    password: password, 
    database: database)

  client.query("start transaction")
rescue Mysql2::Error
    puts "Did you remember to run this the first time with the CREATE_USERS env option?"
    raise
end
