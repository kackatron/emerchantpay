package com.payment.system.controllers;

import com.payment.system.dao.models.User;
import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.payload.request.*;
import com.payment.system.payload.response.LoginResponse;
import com.payment.system.services.user.UserManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestControllersTest {
    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders httpHeaders = new HttpHeaders();

    @Autowired
    UserManagementService userManagementService;

    User testUser;

    @BeforeEach
    public void setup() {
        userManagementService.setCsvFile("classpath:TestUsers.csv");
        testUser = userManagementService.loadUsers().get(0);
        assertNotNull(testUser, "Loading of user failed, it returned Null");
        // This is the only user in TestUsers.csv, on later date make this comparison dynamic.
        assertEquals("Test", testUser.getName());
        assertEquals("test@gmai.com", testUser.getEmail());
    }

    @Test
    public void registerAndRetrieveTransactions() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        httpHeaders.setBearerAuth(acquireJwtToken(testUser.getName(), "test"));
        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomer_email("test@test.tes");
        customerInfo.setCustomer_phone("999-999-999");
        HttpEntity<RegisterTransaction> registerTransaction =
                new HttpEntity<>(new RegisterTransaction("1", "201", "",
                        TransactionManagementController.AUTHORIZATION, customerInfo), httpHeaders);
        ResponseEntity<String> registerResponse = restTemplate.exchange(
                createURLWithPort("/trx/load"), HttpMethod.POST, registerTransaction, String.class);
        assertEquals(HttpStatus.ACCEPTED, registerResponse.getStatusCode());

        HttpEntity<RetrieveTransactionsRequest> retrieveRequest = new HttpEntity<>(new RetrieveTransactionsRequest(""), httpHeaders);
        ResponseEntity<List<Transaction>> retrieveResponse = restTemplate.exchange(
                createURLWithPort("/trx/retrieve"), HttpMethod.POST, retrieveRequest, new ParameterizedTypeReference<List<Transaction>>() {
                });
        assertEquals(HttpStatus.ACCEPTED, retrieveResponse.getStatusCode());
        assertNotNull(retrieveResponse.getBody());
        assertEquals(1, retrieveResponse.getBody().get(0).getUuid(), "This is not the same transaction!");
        assertEquals(201.0, retrieveResponse.getBody().get(0).getAmount(), "This is not the same transaction!");
    }

    @Test
    public void retrieveUsers() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        httpHeaders.setBearerAuth(acquireJwtToken("Mr.Smith", "matrix"));
        HttpEntity<String> retrieveUsers = new HttpEntity<>("none", httpHeaders);
        ResponseEntity<List<User>> response = restTemplate.exchange(createURLWithPort("/usr/retrieve"),
                HttpMethod.POST, retrieveUsers, new ParameterizedTypeReference<List<User>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().size() > 0, "There should be at least one user.");
    }

    @Test
    public void deleteUsers() {
        TestRestTemplate restTemplate = new TestRestTemplate();
        httpHeaders.setBearerAuth(acquireJwtToken("Mr.Smith", "matrix"));
        HttpEntity<String> deleteRequest = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> deleteResponse = restTemplate.exchange(createURLWithPort("/usr/delete/" + "Neo"),
                HttpMethod.GET, deleteRequest, String.class);
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

        HttpEntity<String> retrieveUsers = new HttpEntity<>("none", httpHeaders);
        ResponseEntity<List<User>> retrieveResponse = restTemplate.exchange(createURLWithPort("/usr/retrieve"),
                HttpMethod.POST, retrieveUsers, new ParameterizedTypeReference<List<User>>() {
                });
        assertEquals(4, retrieveResponse.getBody().size(), "Number of Loaded users is different from expected.");

    }


    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private String acquireJwtToken(String userName, String password) {
        // A little hack here, it will take a too much time to figure out a way to get the Test user password in pure text.
        HttpEntity<LoginRequest> loginRequest = new HttpEntity<LoginRequest>(new LoginRequest(userName, password), httpHeaders);
        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                createURLWithPort("/api/auth/authenticate"), HttpMethod.POST, loginRequest, LoginResponse.class);
        LoginResponse loginResponse = response.getBody();
        return loginResponse.getToken();
    }
}
