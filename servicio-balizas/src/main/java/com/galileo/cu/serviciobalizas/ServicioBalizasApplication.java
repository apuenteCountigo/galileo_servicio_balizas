package com.galileo.cu.serviciobalizas;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
@EntityScan({ "com.galileo.cu.commons.models" })
public class ServicioBalizasApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ServicioBalizasApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("**************************************");
		System.out.println("Balizas V-24-10-16 03:50");

	}

}
