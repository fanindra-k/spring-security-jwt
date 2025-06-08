package com.kushwaha.book.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    @GetMapping("/api/user")
    @PreAuthorize("hasRole('USER')")
    public String userEndpoint() {
        return "Hello, USER! You are authenticated and authorized.";
    }

    @GetMapping("/api/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminEndpoint() {
        return "Hello, ADMIN! You are authenticated and authorized.";
    }
}