package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.AdminPublic;
import com.example.storemanagement.dto.request.UpsertUserRequest;
import com.example.storemanagement.enity.Admin;
import com.example.storemanagement.enity.Role;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.exception.UserHandleException;
import com.example.storemanagement.repository.AdminRepository;
import com.example.storemanagement.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    public AdminPublic createUser(UpsertUserRequest createAdminRequest) {
        for(Admin user : adminRepository.findAll()) {
            if(user.getEmail().equals(createAdminRequest.username())) {
                throw new UserHandleException("Username đã tồn tại!");
            }
        }

        Admin admin = new Admin();
        admin.setUsername(createAdminRequest.username());
        admin.setFullName(createAdminRequest.fullName());
        admin.setAddress(createAdminRequest.address());
        admin.setEmail(createAdminRequest.email());
        admin.setPhone(createAdminRequest.phone());
        admin.setPassword(createAdminRequest.password());
        Role role = roleRepository.findByName("ADMIN").orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy role ADMIN");
        });
        admin.setRole(role);
        adminRepository.save(admin);

        return AdminPublic.of(admin);
    }
}
