package com.example.storemanagement.controller;

import com.example.storemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String newPassword,
                                            @RequestParam Integer userId) {
        userService.changePassword(newPassword, userId);
        return ResponseEntity.ok("Thay đổi mật khẩu thành công");
    }

    @PostMapping("/avatar/{userId}")
    public ResponseEntity<?> uploadStaffAvatar(@PathVariable Integer userId,
                                               @RequestParam("file") MultipartFile file) {
        userService.uploadAvatar(file, userId);
        return ResponseEntity.ok("upload thành công");
    }
}
