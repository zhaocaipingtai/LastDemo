package com.example.lalala;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "com.example")
public class LalalaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LalalaApplication.class, args);
    }

}
