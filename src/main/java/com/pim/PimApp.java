package com.pim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.pim.repository")
public class PimApp implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(PimApp.class);

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(PimApp.class, args);
	}

	@Override
	public void run(String... args) {
		testDatabaseConnection();
	}

	private void testDatabaseConnection() {
		try (Connection connection = dataSource.getConnection()) {
			if (connection != null) {
				String dbUrl = connection.getMetaData().getURL();
				logger.info("Successfully connected to the database!");
				logger.info("Database URL: {}", dbUrl);
			} else {
				System.out.println("Failed to connect to the database.");
			}
		} catch (SQLException e) {
			System.out.println("An error occurred while connecting to the database: " + e.getMessage());
		}
	}


}
