package com.example.dslist;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "DSList", version = "1.0"))
@ComponentScan
public class DslistApplication {

	public static void main(String[] args) {
		SpringApplication.run(DslistApplication.class, args);
	}

}
