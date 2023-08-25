package com.example.storemanagement.service;

import com.example.storemanagement.enity.Owner;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.OwnerRepository;
import com.example.storemanagement.repository.StaffRepository;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WebService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OwnerRepository ownerRepository;


    public boolean checkUserHasAccessToStore(String username, Integer storeId) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user.getRole().getId() == 2) {
            List<Store> stores = storeRepository.findByOwnerId(user.getId());
            for (Store store : stores) {
                if (store.getId() == storeId) {
                    return true;
                }
            }
        } else if (user.getRole().getId() == 3) {
            Staff staff = staffRepository.findById(user.getId()).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy nhân viên " + username);
            });
            if (staff.getStore().getId() == storeId) {
                return true;
            }
        }
        return false;
    }

    public List<Store> getListStoreByUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        List<Store> stores = new ArrayList<>();
        if (user.getRole().getId() == 2) {
            stores = storeRepository.findByOwnerId(user.getId());
        } else if (user.getRole().getId() == 3) {
            Staff staff = staffRepository.findById(user.getId()).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy nhân viên " + username);
            });
            stores.add(staff.getStore());
        }
        return stores;
    }
    
    public Object getUserInfo(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> {
            throw new NotFoundException("Không tìm thấy người dùng " + username);
        });
        if (user.getRole().getId() == 2) {
            Owner owner = ownerRepository.findById(user.getId()).orElseThrow(()-> {
                throw new NotFoundException("Không tìm thấy người dùng " + username);
            });
            return owner;
        } else if (user.getRole().getId() == 3) {
            Staff staff = staffRepository.findById(user.getId()).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy nhân viên " + username);
            });
            return staff;
        }
        return null;
    }
}
