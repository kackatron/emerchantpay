package com.payment.system.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentRestController {
    @RequestMapping("/pay")
    public String index() {
        return "Greetings from Spring Boot!";
    }
}
