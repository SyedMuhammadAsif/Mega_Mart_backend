package com.megamart.orderpaymentserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderPaymentServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(OrderPaymentServerApplication.class, args);
		System.out.println("Order Payment Server running successfully");
	}

}
