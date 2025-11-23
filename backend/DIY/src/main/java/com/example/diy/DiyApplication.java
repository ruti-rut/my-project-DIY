package com.example.diy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // <--- הנה! זה המתג שחסר לך!
public class DiyApplication {

    public static void main(String[] args) {

        SpringApplication.run(DiyApplication.class, args);
    }

}
