package com.payment.system.controllers;

import com.payment.system.dao.models.User;
import com.payment.system.payload.request.DeleteUserRequest;
import com.payment.system.services.user.UserManagementService;
import com.payment.system.services.user.UserProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/usr")
public class UserManagementController {

    @Autowired
    UserManagementService userManagementService;

    @RequestMapping("/retrieve")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity retrieveUsers() {
        List<User> listUsers = userManagementService.getAllUsers();
        return ResponseEntity.ok(listUsers);
    }

    @RequestMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity deleteUser(@Valid @RequestBody DeleteUserRequest request) {
        List<User> listUsers = null;
        try {
            userManagementService.deleteUser(request.getName());
        } catch (UserProcessingException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
        }
        return ResponseEntity.ok(listUsers);
    }

}
