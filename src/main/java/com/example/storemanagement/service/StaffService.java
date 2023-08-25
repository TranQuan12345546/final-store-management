package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.StaffPublic;
import com.example.storemanagement.dto.request.UpsertStaffRequest;
import com.example.storemanagement.enity.Image;
import com.example.storemanagement.enity.Role;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.RoleRepository;
import com.example.storemanagement.repository.StaffRepository;
import com.example.storemanagement.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final StoreRepository storeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ImageService imageService;

    public StaffService(StaffRepository staffRepository,
                        RoleRepository roleRepository,
                        StoreRepository storeRepository) {
        this.staffRepository = staffRepository;
        this.roleRepository = roleRepository;
        this.storeRepository = storeRepository;
    }

    public List<StaffPublic> getAllStaffFromStore(Integer storeId) {
        List<Staff> staffs = staffRepository.findAllByStoreId(storeId);
        return staffs.stream().map(StaffPublic::of).collect(Collectors.toList());
    }

    public StaffPublic createNewStaff(Integer storeId, UpsertStaffRequest upsertStaffRequest, MultipartFile file) {
        Staff staff = new Staff();
        staff.setUsername(upsertStaffRequest.getUsername());
        staff.setFullName(upsertStaffRequest.getFullName());
        staff.setPassword(passwordEncoder.encode(upsertStaffRequest.getPassword()));
        staff.setAddress(upsertStaffRequest.getAddress());
        staff.setPhone(upsertStaffRequest.getPhone());
        staff.setBirthday(upsertStaffRequest.getBirthday());
        staff.setEmail(upsertStaffRequest.getEmail());

        Role role = roleRepository.findByName("STAFF").orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy role");
        });
        staff.setRole(role);
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store");
        });
        staff.setStore(store);
        if (file != null) {
            staff.setAvatar(imageService.uploadAvatar(file));
        }
        staffRepository.save(staff);
        return StaffPublic.of(staff);
    }

    public void uploadStaffAvatar(MultipartFile file, Integer staffId) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy nhân viên");
        });;
        staff.setAvatar(imageService.uploadAvatar(file));
        staffRepository.save(staff);
    }

    public StaffPublic updateStaffInfo(UpsertStaffRequest upsertStaffRequest) {
        Staff staff = staffRepository.findById(upsertStaffRequest.getStaffId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy nhân viên");
        });
        String fullName = upsertStaffRequest.getFullName();
        if (fullName != null) {
            staff.setFullName(fullName);
        }
        String username = upsertStaffRequest.getUsername();
        if (username != null) {
            staff.setUsername(username);
        }
        String email = upsertStaffRequest.getEmail();
        if (email != null) {
            staff.setEmail(email);
        }
        String phone = upsertStaffRequest.getPhone();
        if (phone != null) {
            staff.setPhone(phone);
        }
        String birthday = upsertStaffRequest.getBirthday();
        if (birthday != null) {
            staff.setBirthday(birthday);
        }
        staffRepository.save(staff);
        return StaffPublic.of(staff);
    }

    public void deleteStaff(Integer staffId) {
        Staff staff = staffRepository.findById(staffId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy nhân viên");
        });
        staffRepository.delete(staff);
    }
}
