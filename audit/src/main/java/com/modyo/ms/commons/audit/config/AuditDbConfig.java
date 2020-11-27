package com.modyo.ms.commons.audit.config;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "auditEntityManagerFactory",
    transactionManagerRef = "auditTransactionManager",
    basePackages = { "com.modyo.ms.commons.audit.persistence" }
)
@RequiredArgsConstructor
public class AuditDbConfig {

  private static final String PERSISTENCE_ADAPTER_PACKAGE = "com.modyo.ms.commons.audit.persistence";
  private static final String PROPERTIES_PREFIX = "spring.datasource.audit";
  private final Environment env;

  @Bean(name = "auditDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.audit")
  public DataSource auditDataSource() {
    HikariDataSource dataSource
        = new HikariDataSource();
    Optional.ofNullable(env.getProperty(PROPERTIES_PREFIX.concat(".driverClassName")))
        .ifPresent(dataSource::setDriverClassName);
    dataSource.setJdbcUrl(env.getProperty(PROPERTIES_PREFIX.concat(".url")));
    dataSource.setUsername(env.getProperty(PROPERTIES_PREFIX.concat(".username")));
    dataSource.setPassword(env.getProperty(PROPERTIES_PREFIX.concat(".password")));

    Optional.ofNullable(env.getProperty(PROPERTIES_PREFIX.concat(".hikari.connectionTimeout")))
        .map(Long::parseLong)
        .ifPresent(dataSource::setConnectionTimeout);
    Optional.ofNullable(env.getProperty(PROPERTIES_PREFIX.concat(".hikari.idleTimeout")))
        .map(Long::parseLong)
        .ifPresent(dataSource::setIdleTimeout);
    Optional.ofNullable(env.getProperty(PROPERTIES_PREFIX.concat(".hikari.maxLifetime")))
        .map(Long::parseLong)
        .ifPresent(dataSource::setMaxLifetime);
    Optional.ofNullable(env.getProperty(PROPERTIES_PREFIX.concat(".hikari.maximumPoolSize")))
        .map(Integer::parseInt)
        .ifPresent(dataSource::setMaximumPoolSize);

    return dataSource;
  }

  @Bean(name = "auditEntityManagerFactory")
  public LocalContainerEntityManagerFactoryBean
  auditEntityManagerFactory(
      EntityManagerFactoryBuilder builder,
      @Qualifier("auditDataSource") DataSource dataSource
  ) {
    Map<String,String> properties = new HashMap<>();
    properties.put("hibernate.hbm2ddl.auto", env.getProperty(PROPERTIES_PREFIX.concat(".ddl-auto")));
    return builder
        .dataSource(dataSource)
        .packages(PERSISTENCE_ADAPTER_PACKAGE)
        .persistenceUnit("audit")
        .properties(properties)
        .build();
  }

  @Bean(name = "auditTransactionManager")
  public PlatformTransactionManager auditTransactionManager(
      @Qualifier("auditEntityManagerFactory") EntityManagerFactory
          auditEntityManagerFactory
  ) {
    return new JpaTransactionManager(auditEntityManagerFactory);
  }
}
