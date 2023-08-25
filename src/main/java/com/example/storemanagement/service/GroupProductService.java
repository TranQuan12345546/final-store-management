package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.GroupProductPublic;
import com.example.storemanagement.enity.GroupProduct;
import com.example.storemanagement.enity.Supplier;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.GroupProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupProductService {
    @Autowired
    private GroupProductRepository groupProductRepository;

    public List<GroupProductPublic> getAllGroupProductByStore(Integer storeId) {
        List<GroupProduct> groupProductList = groupProductRepository.findAllByStoreId(storeId);
        return groupProductList.stream().map(GroupProductPublic::of).collect(Collectors.toList());
    }

    public GroupProduct getGroupProduct(Integer groupProductId) {
        GroupProduct groupProduct = groupProductRepository.findById(groupProductId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy nhà cung cấp");
        });
        return groupProduct;
    }
}
