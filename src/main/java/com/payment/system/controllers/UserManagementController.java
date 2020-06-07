package com.payment.system.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usr")
public class UserManagementController {

    @RequestMapping("/retrieve")
    public ResponseEntity retrieveUsers() {
        return ResponseEntity.ok().build();
    }
}
