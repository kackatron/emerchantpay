package com.payment.system.controllers.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("index")
    public String index(){
        return "index.html";
    }

    @RequestMapping("/login")
    public String login(){
        return "login.html";
    }
}
