package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.GroupProductPublic;
import com.example.storemanagement.dto.request.UpsertGroupProductRequest;
import com.example.storemanagement.enity.GroupProduct;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.GroupProductRepository;
import com.example.storemanagement.repository.StoreRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group-product")
public class GroupProductController {
    @Autowired
    private GroupProductRepository groupProductRepository;

    @Autowired
    private StoreRepository storeRepository;

    @GetMapping("/all-group/{storeId}")
    public ResponseEntity<?> getAllGroupProduct(@PathVariable Integer storeId) {
        List<GroupProduct> groupProductList = groupProductRepository.findAllByStoreId(storeId);
        List<GroupProductPublic> groupProductPublicList = groupProductList.stream().map(GroupProductPublic::of).collect(Collectors.toList());
        return ResponseEntity.ok(groupProductPublicList);
    }


    @PostMapping("/create")
    public ResponseEntity<?> addGroupProduct(@Valid @RequestBody UpsertGroupProductRequest upsertGroupProductRequest) {
        GroupProduct groupProduct = new GroupProduct();
        groupProduct.setName(upsertGroupProductRequest.getName());
        Store store = storeRepository.findById(upsertGroupProductRequest.getStoreId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy cửa hàng với id " + upsertGroupProductRequest.getStoreId());
        });
        groupProduct.setStore(store);
        groupProductRepository.save(groupProduct);
        GroupProductPublic groupProductPublic = GroupProductPublic.of(groupProduct);
        return ResponseEntity.ok(groupProductPublic);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateGroupProduct(@Valid @RequestBody UpsertGroupProductRequest upsertGroupProductRequest) {
        GroupProduct groupProduct = groupProductRepository.findByIdAndStoreId(upsertGroupProductRequest.getId(), upsertGroupProductRequest.getStoreId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy nhóm hàng với id " + upsertGroupProductRequest.getId());
        });
        groupProduct.setName(upsertGroupProductRequest.getName());
        groupProductRepository.save(groupProduct);
        GroupProductPublic groupProductPublic = GroupProductPublic.of(groupProduct);
        return ResponseEntity.ok(groupProductPublic);
    }
}
