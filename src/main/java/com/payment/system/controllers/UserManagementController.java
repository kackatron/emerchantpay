package com.payment.system.controllers;

import com.payment.system.dao.models.User;
import com.payment.system.services.user.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/usr")
public class UserManagementController {

    @Autowired
    UserManagementService userManagementService;

    @RequestMapping("/retrieve")
    public ResponseEntity retrieveUsers() {
        List<User> listUsers = userManagementService.getAllUsers();
        return ResponseEntity.ok(listUsers);
    }
}
