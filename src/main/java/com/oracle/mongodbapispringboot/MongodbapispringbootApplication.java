package com.oracle.mongodbapispringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * From: https://www.bezkoder.com/spring-boot-mongodb-reactive/
 */
@EnableWebFlux
@SpringBootApplication
public class MongodbapispringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongodbapispringbootApplication.class, args);
	}

}
