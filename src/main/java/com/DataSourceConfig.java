package com;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() throws IOException {
        Properties props = PropertiesLoaderUtils.loadProperties(new FileSystemResource("./.env.properties"));
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(props.getProperty("db.url"));
        dataSource.setUsername(props.getProperty("db.username"));
        dataSource.setPassword(props.getProperty("db.password"));
        return dataSource;
    }
}
