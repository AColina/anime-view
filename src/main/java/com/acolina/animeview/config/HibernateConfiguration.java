
package com.acolina.animeview.config;

import java.util.Properties;

//import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Carga de los datos y configuracion de base de datos.
 * 
 *
 */
//@Configuration
//@EnableTransactionManagement
//@PropertySource(value = { //
//		"classpath:application.properties"	 // DB props
//})
public class HibernateConfiguration {

	/**
	 * 
	 */
	@Autowired
	private Environment environment;

	/**
	 * Configuracion del Session Factory de Hibernet con Spring.
	 * 
	 * @return
	 */
	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

		// sessionFactory.setDataSource(dataSource());

		sessionFactory.setHibernateProperties(hibernateProperties());

		sessionFactory.setPackagesToScan("com.animeview.model");

//		sessionFactory.setEntityInterceptor(new HibernateEntityInterceptor());

		return sessionFactory;
	}

	/**
	 * Configuracion de Hibernate
	 * 
	 * @return
	 */
	private Properties hibernateProperties() {

		Properties properties = new Properties();

		properties.setProperty("hibernate.connection.driver_class", //
				environment.getRequiredProperty("jdbc.driverClassName"));

		properties.setProperty("hibernate.connection.username", //
				environment.getRequiredProperty("jdbc.username"));

		properties.setProperty("hibernate.connection.url", //
				environment.getRequiredProperty("jdbc.url"));

		properties.setProperty("hibernate.connection.password", //
				environment.getRequiredProperty("jdbc.password"));

		properties.put("hibernate.dialect", //
				environment.getRequiredProperty("hibernate.dialect"));

		properties.put("hibernate.show_sql", //
				environment.getRequiredProperty("hibernate.show_sql"));

		properties.put("hibernate.format_sql", //
				environment.getRequiredProperty("hibernate.format_sql"));

		properties.put("hibernate.generate_statistics",
				environment.getRequiredProperty("hibernate.generate_statistics"));

		properties.put("hbm2ddl.auto", //
				environment.getRequiredProperty("hbm2ddl.auto"));

		properties.put("current_session_context_class", //
				environment.getRequiredProperty("current_session_context_class"));

		properties.put("hibernate.use_sql_comments", //
				environment.getRequiredProperty("hibernate.use_sql_comments"));

		properties.put("hibernate.order_updates", //
				environment.getRequiredProperty("hibernate.order_updates"));

		// properties.put("hibernate.transaction.auto_close_session", "true");

		properties.put("hibernate.connection.pool_size", //
				environment.getRequiredProperty("hibernate.connection.pool_size"));

		// acelera el startup
		properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");

		properties.put("javax.persistence.validation.mode",
				environment.getRequiredProperty("javax.persistence.validation.mode"));

		properties.put("hibernate.validator.autoregister_listeners",
				environment.getRequiredProperty("hibernate.validator.autoregister_listeners"));

		properties.put("hibernate.validator.apply_to_ddl",
				environment.getRequiredProperty("hibernate.validator.apply_to_ddl"));

		properties.put("hibernate.connection.provider_class", //
				"org.hibernate.c3p0.internal.C3P0ConnectionProvider");

		properties.put("hibernate.c3p0.min_size", //
				environment.getRequiredProperty("hibernate.c3p0.min_size"));

		properties.put("hibernate.c3p0.max_size", //
				environment.getRequiredProperty("hibernate.c3p0.max_size"));

		properties.put("hibernate.c3p0.timeout", //
				environment.getRequiredProperty("hibernate.c3p0.timeout"));

		properties.put("hibernate.c3p0.max_statements", //
				environment.getRequiredProperty("hibernate.c3p0.max_statements"));

		properties.put("hibernate.c3p0.idle_test_period", //
				environment.getRequiredProperty("hibernate.c3p0.idle_test_period"));

		properties.put("hibernate.cache.use_second_level_cache", "true");

		properties.put("hibernate.cache.region.factory_class", //
				"org.hibernate.cache.ehcache.EhCacheRegionFactory");

		properties.put("hibernate.cache.use_query_cache", "true");
		
		properties.put("hibernate.enable_lazy_load_no_trans", "true");

		return properties;

	}

	/**
	 * 
	 * @param s
	 * @return
	 */
//	@Bean
//	@Autowired
//	public HibernateTransactionManager transactionManager(SessionFactory s) {
//		HibernateTransactionManager txManager = new HibernateTransactionManager();
//		txManager.setSessionFactory(s);
//		return txManager;
//	}

}
