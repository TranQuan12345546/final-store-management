package com.example.storemanagement;

import com.example.storemanagement.enity.Admin;
import com.example.storemanagement.enity.Role;
import com.example.storemanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class StoreManagementApplication implements CommandLineRunner {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AdminRepository adminRepository;

	public static void main(String[] args) {
		SpringApplication.run(StoreManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Tạo vai trò "admin" nếu chưa tồn tại
		Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
		if (adminRole == null) {
			adminRole = new Role();
			adminRole.setName("ADMIN");
			roleRepository.save(adminRole);
		}

		Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
		if (ownerRole == null) {
			ownerRole = new Role();
			ownerRole.setName("OWNER");
			roleRepository.save(ownerRole);
		}

		Role staffRole = roleRepository.findByName("STAFF").orElse(null);
		if (staffRole == null) {
			staffRole = new Role();
			staffRole.setName("STAFF");
			roleRepository.save(staffRole);
		}

		// Tạo tài khoản admin nếu chưa tồn tại
		Admin adminUser = adminRepository.findByUsername("admin");
		if (adminUser == null) {
			adminUser = new Admin();
			adminUser.setUsername("admin");
			adminUser.setPassword(passwordEncoder.encode("111"));
			adminUser.setRole(adminRole);
			adminRepository.save(adminUser);
		}
	}
}
