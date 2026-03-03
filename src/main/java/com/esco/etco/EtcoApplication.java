package com.esco.etco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EtcoApplication {

	public static void main(String[] args) {
		// Log heap size thực tế để verify JVM args
		Runtime rt = Runtime.getRuntime();
		System.out.println("====================================");
		System.out.println(">>> JVM Max Heap: " + (rt.maxMemory() / 1024 / 1024) + " MB");
		System.out.println(">>> JVM Total Heap: " + (rt.totalMemory() / 1024 / 1024) + " MB");
		System.out.println("====================================");

		SpringApplication.run(EtcoApplication.class, args);
	}

}
