package com.example.storemanagement.controller;

import com.example.storemanagement.dto.request.RegisterOwner;
import com.example.storemanagement.enity.TokenConfirm;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.exception.UserHandleException;
import com.example.storemanagement.repository.TokenConfirmRepository;
import com.example.storemanagement.repository.UserRepository;
import com.example.storemanagement.service.MailService;
import com.example.storemanagement.service.OwnerService;
import com.example.storemanagement.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RestController
public class OwnerController {
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private TokenConfirmRepository tokenConfirmRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterOwner registerOwner) {
        Integer storeId = ownerService.createOwnerAndStore(registerOwner);
        return ResponseEntity.ok(storeId);
    }

    @PostMapping("/send-resister-code")
    public ResponseEntity<?> resisterOwner(@RequestParam String email) throws MessagingException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            StringBuilder digits = new StringBuilder();
            Random random = new Random();

            for (int i = 0; i < 8; i++) {
                int digit = random.nextInt(10);
                digits.append(digit);
            }

            String token = digits.toString();

            TokenConfirm tokenConfirm = TokenConfirm.builder()
                    .token(token)
                    .createdAt(LocalDateTime.now())
                    .expiredAt(LocalDateTime.now().plusMinutes(30))
                    .build();
            tokenConfirmRepository.save(tokenConfirm);
            mailService.sendResisterCode(
                    email,
                    tokenConfirm.getToken()
            );
        } else {
            return ResponseEntity.badRequest().body("Email này đã được đăng ký");
        }

        return ResponseEntity.ok("Send mail success");
    }

    @GetMapping("/resister/{token}")
    public ResponseEntity<?> checkToken(@PathVariable String token) {
        // Kiểm tra token có hợp lệ hay không
        Optional<TokenConfirm> optionalTokenConfirm = tokenConfirmRepository.findByToken(token);
        if(optionalTokenConfirm.isEmpty()) {
            return ResponseEntity.badRequest().body("Mã xác thực không hợp lệ");
        }
        // Kiểm tra token đã được kích hoạt hay chưa
        TokenConfirm tokenConfirm = optionalTokenConfirm.get();
        if(tokenConfirm.getConfirmedAt() != null) {
            return ResponseEntity.badRequest().body("Mã xác thực đã được sử dụng");
        }

        // Kiểm tra token đã hết hạn hay chưa
        if(tokenConfirm.getExpiredAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Mã xác thực đã hết hạn");
        }

        tokenConfirm.setConfirmedAt(LocalDateTime.now());
        tokenConfirmRepository.save(tokenConfirm);
        return ResponseEntity.ok("Xác nhận thành công");
    }



}
