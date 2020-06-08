package com.payment.system.controllers.ui;

import com.payment.system.dao.models.User;
import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.security.UserDetailsImpl;
import com.payment.system.services.trx.TransactionRetrievalException;
import com.payment.system.services.user.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Admin controller is a specialized controller that handles the Administrator page.
 * It provides attribute loading so that Spring Tymeleaf EL can do its thing.
 */
@Controller
@RequestMapping("admin")
public class AdminProfileController {

    @Autowired
    UserManagementService userManagementService;

    @GetMapping("index")
    public String index(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<User> userList = userManagementService.getAllUsers();
        model.addAttribute("users", userList);
        model.addAttribute("currentUserDetails",userDetails);
        return "admin/index";
    }
}
