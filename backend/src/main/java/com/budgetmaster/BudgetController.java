package com.budgetmaster;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class BudgetController {
	
	@GetMapping("hello")
	public String sayHello() {
		return "Hello, BudgetMaster!";
	}
}