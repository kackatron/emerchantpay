package com.payment.system.controllers;

import com.payment.system.dao.models.User;
import com.payment.system.services.user.UserManagementService;
import com.payment.system.services.user.UserProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * UserManagementController covers all the user management operations.
 */
@RestController
@RequestMapping("/usr")
public class UserManagementController {

    @Autowired
    UserManagementService userManagementService;

    /**
     * Returns all the users
     * @return
     */
    @RequestMapping("/retrieve")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity retrieveUsers() {
        List<User> listUsers = userManagementService.getAllUsers();
        return ResponseEntity.ok(listUsers);
    }

    /**
     * Delete the specified user
     * @param name
     * @return
     */
    @RequestMapping("/delete/{name}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity deleteUser(@PathVariable(name = "name") String name) {
        List<User> listUsers = null;
        try {
            userManagementService.deleteUser(name);
        } catch (UserProcessingException e) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e);
        }
        return ResponseEntity.ok(listUsers);
    }

}
