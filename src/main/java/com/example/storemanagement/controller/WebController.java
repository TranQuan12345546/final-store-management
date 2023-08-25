package com.example.storemanagement.controller;

import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.TokenConfirm;
import com.example.storemanagement.repository.TokenConfirmRepository;
import com.example.storemanagement.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    private WebService webService;
    @Autowired
    private TokenConfirmRepository tokenConfirmRepository;

    @GetMapping("/")
    public String getHome() {
        return "index";
    }

    @GetMapping("/login")
    public String getLoginPage(Authentication authentication) {
        if(authentication != null && authentication.isAuthenticated()) {
            List<Store> stores = webService.getListStoreByUser(authentication.getName());
            return "redirect:/products/" + stores.get(0).getId() + "/overview";
        }
        return "login";
    }

    @GetMapping("/forgot-password")
    public String getForgotPasswordPage() {
        return "forgot-password";
    }

    @GetMapping("/doi-mat-khau/{token}")
    public String getUpdatePasswordPage(@PathVariable String token, Model model) {
        // Kiểm tra token có hợp lệ hay không
        Optional<TokenConfirm> optionalTokenConfirm = tokenConfirmRepository.findByToken(token);
        if(optionalTokenConfirm.isEmpty()) {
            model.addAttribute("isValid", false);
            model.addAttribute("message", "Token không hợp lệ");
            return "update-password";
        }
        // Kiểm tra token đã được kích hoạt hay chưa
        TokenConfirm tokenConfirm = optionalTokenConfirm.get();
        if(tokenConfirm.getConfirmedAt() != null) {
            model.addAttribute("isValid", false);
            model.addAttribute("message", "Token đã được kích hoạt");
            return "update-password";
        }

        // Kiểm tra token đã hết hạn hay chưa
        if(tokenConfirm.getExpiredAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("isValid", false);
            model.addAttribute("message", "Token đã hết hạn");
            return "update-password";
        }

        tokenConfirm.setConfirmedAt(LocalDateTime.now());
        tokenConfirmRepository.save(tokenConfirm);
        model.addAttribute("isValid", true);
        model.addAttribute("userIda", tokenConfirm.getUser().getId());
        return "update-password";
    }


}
