package com.klu.fsd.sdp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SdpbackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdpbackendApplication.class, args);
		System.out.println("Quiz Application backend is running!");
		}
}
