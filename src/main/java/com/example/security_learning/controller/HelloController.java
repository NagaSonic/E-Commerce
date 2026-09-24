package com.example.security_learning.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides a basic endpoint used to verify that the application is running.
 */
@RestController
public class HelloController {

    /**
     * Returns a simple greeting to verify the application endpoint.
     *
     * @return greeting message
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Security!";
    }
}
