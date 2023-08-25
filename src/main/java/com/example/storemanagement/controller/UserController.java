package com.example.storemanagement.controller;

import com.example.storemanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Operation(summary = "Thay đổi mật khẩu người dùng")
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String newPassword,
                                            @RequestParam Integer userId) {
        userService.changePassword(newPassword, userId);
        return ResponseEntity.ok("Thay đổi mật khẩu thành công");
    }

    @Operation(summary = "Thay đổi avatar người dùng")
    @PostMapping("/avatar/{userId}")
    public ResponseEntity<?> uploadStaffAvatar(@PathVariable Integer userId,
                                               @RequestParam("file") MultipartFile file) {
        userService.uploadAvatar(file, userId);
        return ResponseEntity.ok("upload thành công");
    }

    @GetMapping("/check-old-password/{username}")
    public ResponseEntity<?> checkOldPassword(@PathVariable String username, @RequestParam String password) {
        userService.checkOldPassword(password, username);
        return ResponseEntity.ok("Trùng khớp");
    }
}
