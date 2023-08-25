package com.example.storemanagement.service;

import com.example.storemanagement.dto.request.RegisterOwner;
import com.example.storemanagement.enity.Owner;
import com.example.storemanagement.enity.SalaryPerHour;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.OwnerRepository;
import com.example.storemanagement.repository.RoleRepository;
import com.example.storemanagement.repository.SalaryPerHourRepository;
import com.example.storemanagement.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    private final RoleRepository roleRepository;
    private final OwnerRepository ownerRepository;
    private final StoreRepository storeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final SalaryPerHourRepository salaryPerHourRepository;

    public OwnerService(RoleRepository roleRepository,
                        OwnerRepository ownerRepository,
                        StoreRepository storeRepository,
                        SalaryPerHourRepository salaryPerHourRepository) {
        this.roleRepository = roleRepository;
        this.ownerRepository = ownerRepository;
        this.storeRepository = storeRepository;
        this.salaryPerHourRepository = salaryPerHourRepository;
    }

    public Integer createOwnerAndStore(RegisterOwner registerOwner) {
        Owner owner = new Owner();
        owner.setFullName(registerOwner.getFullName());
        owner.setUsername(registerOwner.getUsername());
        owner.setPassword(passwordEncoder.encode(registerOwner.getPassword()));
        System.out.println(registerOwner.getAddress());
        owner.setAddress(registerOwner.getAddress());
        owner.setEmail(registerOwner.getEmail());
        owner.setRole(roleRepository.findByName("OWNER").orElse(null));
        owner.setPhone(registerOwner.getPhone());
        ownerRepository.save(owner);
        Store store = new Store();
        store.setName(registerOwner.getNameStore());
        store.setOwner(owner);
        storeRepository.save(store);
        SalaryPerHour salary = new SalaryPerHour();
        salary.setHourlySalary(50000);
        salary.setStore(store);
        salaryPerHourRepository.save(salary);
        return store.getId();
    }

    public String getOwnerStoreName(Integer storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store nào có id " + storeId);
        });
        return store.getOwner().getFullName();
    }
}
