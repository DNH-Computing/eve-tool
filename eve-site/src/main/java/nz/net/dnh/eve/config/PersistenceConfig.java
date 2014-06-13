package nz.net.dnh.eve.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="nz.net.dnh.eve")
public class PersistenceConfig implements TransactionManagementConfigurer {
	
	@Value("${dataSource.driverClassName}")
	private String driver;
	@Value("${dataSource.url}")
	private String url;
	@Value("${dataSource.username}")
	private String username;
	@Value("${dataSource.password}")
	private String password;
	@Value("${hibernate.dialect}")
	private String dialect;
	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddlAuto;

	@Bean
	public DataSource configureDataSource() {
		final ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(this.driver);
		} catch (final PropertyVetoException e) {
			throw new IllegalStateException(e);
		}
		dataSource.setJdbcUrl(this.url);
		dataSource.setUser(this.username);
		dataSource.setPassword(this.password);
		return dataSource;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		entityManagerFactoryBean.setDataSource(configureDataSource());
		entityManagerFactoryBean.setPackagesToScan("nz.net.dnh.eve");
		entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
		
		final Properties jpaProperties = new Properties();
		jpaProperties.put(Environment.DIALECT, this.dialect);
		jpaProperties.put(Environment.HBM2DDL_AUTO, this.hbm2ddlAuto);
		entityManagerFactoryBean.setJpaProperties(jpaProperties);
		
		return entityManagerFactoryBean;
	}

	@Bean(name="transactionManager")
	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return new JpaTransactionManager();
	}

}
