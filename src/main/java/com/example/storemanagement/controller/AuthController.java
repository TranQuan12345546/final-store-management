package com.example.storemanagement.controller;

import com.example.storemanagement.dto.request.LoginRequest;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.TokenConfirm;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.StaffRepository;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.TokenConfirmRepository;
import com.example.storemanagement.repository.UserRepository;
import com.example.storemanagement.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private TokenConfirmRepository tokenConfirmRepository;

    @Autowired
    private MailService mailService;

    @PostMapping("/login-handle")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        // Tạo đối tượng xác thực
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );

        try {
            // Tiến hành xác thực
            Authentication authentication = authenticationManager.authenticate(token);
            // Lưu vào Context Holder
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lưu vào trong session
            session.setAttribute("MY_SESSION", authentication.getName()); // Lưu email -> session
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            List<Store> stores = new ArrayList<>();
            if (user.getRole().getId() == 2) {
                stores = storeRepository.findByOwnerId(user.getId());
            } else if (user.getRole().getId() == 3) {
                Staff staff = staffRepository.findById(user.getId()).orElseThrow(() -> {
                    throw new NotFoundException("Không tìm thấy nhân viên");
                });
                Store store = staff.getStore();
                stores.add(store);
            }
            String responseString = "/products/" + stores.get(0).getId().toString() + "/overview";
            return ResponseEntity.ok(responseString);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login fail");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) throws MessagingException {
        // Kiểm tra email gửi lên có tồn tại trong db hay không
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new RuntimeException("Not found user");
                });

        // Tạo ra token -> lưu vào cơ sở dữ liệu
        // Token là chuỗi UUID
        TokenConfirm tokenConfirm = TokenConfirm.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .user(user)
                .build();
        tokenConfirmRepository.save(tokenConfirm);
        mailService.sendConfirmTokenResetPassword(
                user.getEmail(),
                user.getFullName(),
                "http://localhost:8080/doi-mat-khau/" + tokenConfirm.getToken()
        );
        return ResponseEntity.ok("Send mail success");
    }
}
