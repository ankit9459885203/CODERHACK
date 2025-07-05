package com.crio.coderhack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@EnableMongoRepositories
public class CoderhackApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoderhackApplication.class, args);
    }

}