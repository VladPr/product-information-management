package com.pim;

import com.pim.config.DataSourceConfig;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataSourceConfigTest {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfigTest.class);

    @Test
    public void testDataSource() throws IOException {
        logger.info("Starting testDataSource test");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataSourceConfig.class);
        DataSource dataSource = context.getBean(DataSource.class);

        Properties props = PropertiesLoaderUtils.loadProperties(new FileSystemResource("./.env.properties"));

        assertNotNull(dataSource);
        assertEquals(DriverManagerDataSource.class, dataSource.getClass());

        DriverManagerDataSource driverManagerDataSource = (DriverManagerDataSource) dataSource;
        assertEquals(props.getProperty("db.url"), driverManagerDataSource.getUrl());
        assertEquals(props.getProperty("db.username"), driverManagerDataSource.getUsername());
        assertEquals(props.getProperty("db.password"), driverManagerDataSource.getPassword());

        logger.info("DataSource URL: {}", driverManagerDataSource.getUrl());
        logger.info("DataSource Username: {}", driverManagerDataSource.getUsername());
        logger.info("DataSource Password: {}", driverManagerDataSource.getPassword());

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String testQuery = "SELECT 1";
        logger.info("Executing test query: {}", testQuery);
        Integer result = jdbcTemplate.queryForObject(testQuery, Integer.class);
        assertEquals(Integer.valueOf(1), result);
        logger.info("Query result: {}", result);

        context.close();
        logger.info("Finished testDataSource test");
    }
}