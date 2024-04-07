package com.example.security1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestApiController {

    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }

    @PostMapping("/token")
    public String token() {
        return "<h1>token</h1>";
    }

    @GetMapping("/api/v1/user")
    public String userV1() {
        return "user";
    }

    @GetMapping("/api/v1/manager")
    public String managerV1() {
        return "manager";
    }

    @GetMapping("/api/v1/admin")
    public String adminV1() {
        return "admin";
    }


}

