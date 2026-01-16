package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(scanBasePackages = "org.example")
public class knotApplication {
    public static void main(String[] args) {
        SpringApplication.run(knotApplication.class, args);
    }

    @RestController
    class TestController {
        @GetMapping("/ping")
        public String ping() { return "pong"; }
    }


}
