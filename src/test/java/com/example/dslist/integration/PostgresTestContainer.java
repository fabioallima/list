package com.example.dslist.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

public class PostgresTestContainer implements BeforeAllCallback {

    static AtomicBoolean containerStarted = new AtomicBoolean(false);

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.postgres.jdbc-url", postgres::getJdbcUrl);
        registry.add("spring.datasource.postgres.username", postgres::getUsername);
        registry.add("spring.datasource.postgres.password", postgres::getPassword);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!containerStarted.get()) {
//            postgres.withInitScript(
//                "init-schema.sql"
//            );
            postgres.start();

            System.setProperty("spring.datasource.postgres.jdbc-url", postgres.getJdbcUrl());
            System.setProperty("spring.datasource.postgres.username", postgres.getUsername());
            System.setProperty("spring.datasource.postgres.password", postgres.getPassword());

            DataSourceProperties prop = new DataSourceProperties();
            prop.setDriverClassName(postgres.getDriverClassName());
            prop.setUrl(postgres.getJdbcUrl());
            prop.setUsername(postgres.getUsername());
            prop.setPassword(postgres.getPassword());
            DataSource dataSource = prop.initializeDataSourceBuilder().build();

            Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                //.defaultSchema("mobilidade")
                .locations("classpath:db/migration", "classpath:db/tests")
                .baselineOnMigrate(true)
                .load();
            flyway.migrate();

            containerStarted.set(true);
        }
    }
}
