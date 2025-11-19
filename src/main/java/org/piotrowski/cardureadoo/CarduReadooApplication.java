package org.piotrowski.cardureadoo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarduReadooApplication {

    @Value("${app.security.bootstrap.token}")
    private String setupToken;

    public static void main(String[] args) {
        SpringApplication.run(CarduReadooApplication.class, args);
    }
}
