package com.payment.system.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.system.dao.models.User;
import com.payment.system.payload.request.CustomerInfo;
import com.payment.system.payload.request.LoginRequest;
import com.payment.system.payload.request.RegisterTransaction;
import com.payment.system.payload.response.LoginResponse;
import com.payment.system.services.user.UserLoadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestControllersTest {
    private static final Logger logger = LoggerFactory.getLogger(RestControllersTest.class);
    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders httpHeaders = new HttpHeaders();

    @Autowired
    UserLoadService userLoadService;

    @Autowired
    PasswordEncoder passEncoder;

    User testUser;

    @BeforeEach
    public void setup() {
        userLoadService.setCsvFile("classpath:TestUsers.csv");
        testUser = userLoadService.loadUsers().get(0);
        assertNotNull(testUser, "Loading of user failed, it returned Null");
        // This is the only user in TestUsers.csv, on later date make this comparison dynamic.
        assertEquals("Test", testUser.getName());
        assertEquals("test@gmai.com", testUser.getEmail());
    }

    @Test
    public void loadTransactions() throws Exception {
        TestRestTemplate restTemplate = new TestRestTemplate();
        // A little hack here, it will take a too much time to figure out a way to get the Test user password in pure text.
        HttpEntity<LoginRequest> loginRequest = new HttpEntity<LoginRequest>(new LoginRequest(testUser.getName(), "test"), httpHeaders);
        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                createURLWithPort("/api/auth/signin"), HttpMethod.POST, loginRequest, LoginResponse.class);
        LoginResponse loginResponse = response.getBody();
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomer_email("test@test.tes");
        customerInfo.setCustomer_phone("999-999-999");
        httpHeaders.setBearerAuth(loginResponse.getToken());
        HttpEntity<RegisterTransaction> registerTransaction =
                new HttpEntity<>(new RegisterTransaction("1", "201", "",
                        TransactionManagementController.AUTHORIZATION, customerInfo), httpHeaders);
        ResponseEntity<String> registerResponse = restTemplate.exchange(
                createURLWithPort("/trx/load"), HttpMethod.POST, registerTransaction, String.class);
        assertEquals(HttpStatus.ACCEPTED, registerResponse.getStatusCode());
        logger.info(String.valueOf(response.getStatusCode()));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
