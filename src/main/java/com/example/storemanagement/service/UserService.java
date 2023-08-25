package com.example.storemanagement.service;

import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void changePassword(String newPassword, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy người dùng với id " + userId);
        });
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public void uploadAvatar(MultipartFile file, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy người dùng");
        });
        user.setAvatar(imageService.uploadAvatar(file));
        userRepository.save(user);
    }
}
