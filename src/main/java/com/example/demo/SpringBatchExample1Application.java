package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class SpringBatchExample1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchExample1Application.class, args);
	}

}

// Rest controller is the trigger point 