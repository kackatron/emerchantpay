package com.payment.system.controllers.ui;

import com.payment.system.dao.models.trx.Transaction;
import com.payment.system.security.UserDetailsImpl;
import com.payment.system.services.trx.TransactionRetrievalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


/**
 * MerchantProfileController is a specialized controller that handles the Merchant page.
 * It provides attribute loading so that Spring Tymeleaf EL can do its thing.
 */
@Controller
@RequestMapping("profile")
public class MerchantProfileController {

    @Autowired
    com.payment.system.services.trx.TransactionRetrievalService TransactionRetrievalService;

    @GetMapping("index")
    public String index(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<Transaction> transactionList;
        try {
            transactionList = TransactionRetrievalService.retrieveTransactionsForUser(userDetails);
        } catch (TransactionRetrievalException e) {
            return e.toString();
        }
        model.addAttribute("transactions",transactionList);
        model.addAttribute("currentUserDetails",userDetails);
        return "merchant/index";
    }
}
