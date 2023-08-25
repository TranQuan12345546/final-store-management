package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.SupplierPublic;
import com.example.storemanagement.dto.request.UpsertSupplierRequest;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.Supplier;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private StoreRepository storeRepository;

    public List<SupplierPublic> getAllSupplierRelatedStore(Integer storeId) {
        List<Supplier> supplierList = supplierRepository.findAllByStoreId(storeId);
        return supplierList.stream().map(SupplierPublic::of).collect(Collectors.toList());
    }

    public Supplier getSupplier(Integer supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy nhà cung cấp");
        });
        return supplier;
    }



    public SupplierPublic addSupplier(Integer storeId, UpsertSupplierRequest upsertSupplierRequest) {
        Supplier supplier = new Supplier();
        supplier.setName(upsertSupplierRequest.getName());
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy cửa hàng với id" + storeId);
        });
        supplier.setStore(store);
        supplier.setEmail(upsertSupplierRequest.getEmail());
        supplier.setAddress(upsertSupplierRequest.getAddress());
        supplier.setPhone(upsertSupplierRequest.getPhone());
        supplierRepository.save(supplier);
        return SupplierPublic.of(supplier);
    }

}
