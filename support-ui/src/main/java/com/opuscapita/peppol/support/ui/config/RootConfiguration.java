package com.opuscapita.peppol.support.ui.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.5.2
 * Time: 10:17
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@EnableTransactionManagement
//@EnableCaching
@ComponentScan(basePackages = {"com.opuscapita.peppol.support-ui"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, value = {WebPackageFilter.class})})
@PropertySource({"file:${PEPPOL_CONFIG_PATH}/jdbc.properties", "file:${PEPPOL_CONFIG_PATH}/config.properties"})
public class RootConfiguration {
    private static final Logger logger = Logger.getLogger(RootConfiguration.class);

    @Autowired
    private Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ComboPooledDataSource dataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass(environment.getProperty("jdbc.driverClassName"));
        } catch (java.beans.PropertyVetoException e) {
            logger.error("Failed to initialize DriverClass: " + e.getMessage());
        }
        dataSource.setJdbcUrl(environment.getProperty("jdbc.connectionURL"));
        dataSource.setUser(environment.getProperty("jdbc.username"));
        dataSource.setPassword(environment.getProperty("jdbc.password"));
        dataSource.setAcquireIncrement(Integer.parseInt(environment.getProperty("acquireIncrement")));
        dataSource.setMinPoolSize(Integer.parseInt(environment.getProperty("minPoolSize")));
        dataSource.setMaxPoolSize(Integer.parseInt(environment.getProperty("maxPoolSize")));
        dataSource.setMaxIdleTime(Integer.parseInt(environment.getProperty("maxIdleTime")));
        dataSource.setCheckoutTimeout(Integer.parseInt(environment.getProperty("timeout")));

        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.show_sql", environment.getProperty("jdbc.show_sql"));
        hibernateProperties.setProperty("hibernate.dialect", environment.getProperty("jdbc.dialectClass"));
        hibernateProperties.setProperty("hibernate.connection.charSet", "UTF-8");
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
        hibernateProperties.setProperty("hibernate.max_fetch_depth", "4");
        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        hibernateProperties.setProperty("hibernate.generate_statistics", "true");
        hibernateProperties.setProperty("hibernate.cache.use_structured_entries", "true");
        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
