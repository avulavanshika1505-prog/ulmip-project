package com.ulmip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UlmipApplication {
    public static void main(String[] args) {
        SpringApplication.run(UlmipApplication.class, args);
        System.out.println("🧠 ULMIP Backend Running on http://localhost:8080");
    }
}
