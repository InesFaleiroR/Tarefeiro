package com.tarefeiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TarefeiroApplication {
    public static void main(String[] args) {
        SpringApplication.run(TarefeiroApplication.class, args);
    }
}
