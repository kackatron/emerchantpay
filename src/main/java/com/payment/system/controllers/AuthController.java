package com.payment.system.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.payment.system.dao.models.EUserStatus;
import com.payment.system.dao.models.Role;
import com.payment.system.dao.models.User;
import com.payment.system.dao.repositories.user.RoleRepository;
import com.payment.system.dao.repositories.user.UserRepository;
import com.payment.system.payload.request.LoginRequest;
import com.payment.system.payload.request.RegisterUserRequest;
import com.payment.system.payload.response.LoginResponse;
import com.payment.system.payload.response.SimpleResponse;
import com.payment.system.security.JwtHandler;
import com.payment.system.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtHandler jwtHandler;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtHandler.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new LoginResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getDescription(),
                userDetails.getStatus(),
                userDetails.getTotalTransactionSum(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterUserRequest signUpRequest) {
        if (userRepository.existsByName(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new SimpleResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new SimpleResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        String strRoles = signUpRequest.getRole();

        Role userRole;
        if (strRoles == null) {
            userRole = roleRepository.findByName("")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        } else {
            userRole = roleRepository.findByName(strRoles)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }

        user.setRole(userRole);
        //Always create users active they can be deactivated later
        user.setStatus(EUserStatus.ACTIVE);
        user.setDescription(signUpRequest.getDescription());
        userRepository.save(user);
        return ResponseEntity.ok(new SimpleResponse("User registered successfully!"));
    }
}