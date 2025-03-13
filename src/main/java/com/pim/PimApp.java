package com.pim;

import com.pim.util.ColdStart;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.pim.repository")
public class PimApp {

	private static final Logger logger = LoggerFactory.getLogger(PimApp.class);


	public static void main(String[] args) {

		ApplicationContext ctx = SpringApplication.run(PimApp.class, args);

		String[] beans = ctx.getBeanDefinitionNames();
		//logger.info("Loaded Beans: {}", Arrays.toString(beans));


		if (args.length > 0 && "-coldstart".equals(args[0])) {
			logger.info("Running cold start...");
			if (ctx.getBeanNamesForType(ColdStart.class).length > 0) {  // More reliable check
				ColdStart coldStart = ctx.getBean(ColdStart.class);
				coldStart.runColdStart();
				logger.info("ColdStart beans found ");
			} else {
				logger.error("ColdStart bean not found");
			}


		} else {
			logger.info("Starting application without cold start.");
		}

	}

}