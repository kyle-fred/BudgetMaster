package com.budgetmaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.budgetmaster.*")
@ComponentScan(basePackages = { "com.budgetmaster.*" })
@EntityScan("com.budgetmaster.*")
public class BudgetMasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(BudgetMasterApplication.class, args);
	}

}
