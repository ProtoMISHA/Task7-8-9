package com.example.task789.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.sql.DataSource;

@Configuration
public class HikariDataSourceConfiguration {

    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    @Order(20)
    public HikariDataSource hikariDataSource() {

        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(dataSource());
        return hikariDataSource;
    }


    @Bean
    @Order(10)
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder
                .create()
                .password(password)
                .username(username)
                .url(url)
                .driverClassName(driverClassName)
                .build();

        return dataSource;
    }
}
