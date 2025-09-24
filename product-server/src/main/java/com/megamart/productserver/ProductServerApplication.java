package com.megamart.productserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProductServerApplication.class, args);
		System.out.println("ProductServer run successfully");
	}

}
