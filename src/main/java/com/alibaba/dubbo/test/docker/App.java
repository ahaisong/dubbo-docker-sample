package com.alibaba.dubbo.test.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;

@SpringBootApplication
@ImportResource("classpath:dubbo-docker-provider.xml")
public class App {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(App.class, args);
		System.in.read();
	}
}
