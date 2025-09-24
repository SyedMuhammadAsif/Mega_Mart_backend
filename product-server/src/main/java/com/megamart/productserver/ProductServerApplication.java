package com.megamart.productserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProductServerApplication.class, args);
		System.out.println("ProductServer run successfully");
	}

}
