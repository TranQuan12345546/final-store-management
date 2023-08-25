package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.InventoryProductPublic;
import com.example.storemanagement.dto.projection.InventoryPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.request.CreateInventoryRequest;
import com.example.storemanagement.enity.*;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductHistoryService productHistoryService;
    @Autowired
    private InventoryProductRepository inventoryProductRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private UserRepository userRepository;

    public void createInventory(CreateInventoryRequest createInventoryRequest, Integer storeId) {
        Inventory inventory = new Inventory();
        String inventoryName = createInventoryRequest.getInventoryName();
        if (inventoryName == null || inventoryName == "") {
            Long count = inventoryRepository.count() + 1;
            inventoryName = "Lần kiểm kê số " + count;
        }
        inventory.setInventoryName(inventoryName);
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy cửa hàng có id" + storeId);
        });
        inventory.setStore(store);

        if (createInventoryRequest.getUserId() != null) {
            User user = userRepository.findById(createInventoryRequest.getUserId()).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy người dùng có id " + createInventoryRequest.getUserId());
            });
            inventory.setCreatedBy(user.getFullName());
        }
        inventoryRepository.save(inventory);
        List<InventoryProduct> inventoryProducts = new ArrayList<>();
        List<CreateInventoryRequest.Product> products = createInventoryRequest.getProducts();
        for (CreateInventoryRequest.Product product : products) {
            Product product1 = productRepository.findById(product.getProductId()).orElseThrow(()-> {
                throw new NotFoundException("Không tìm thấy sản phẩm có id" + product.getProductId());
            });
            InventoryProduct inventoryProduct = new InventoryProduct();
            inventoryProduct.setProduct(product1);
            inventoryProduct.setInventory(inventory);
            inventoryProduct.setProductName(product1.getName());
            inventoryProduct.setCodeNumber(product1.getCodeNumber());
            inventoryProduct.setGroupName(product1.getGroupProduct().getName());
            inventoryProduct.setInitialPrice(product1.getInitialPrice());
            inventoryProduct.setQuantityBeforeCheck(product.getQuantityBeforeCheck());
            inventoryProduct.setQuantityAfterCheck(product.getQuantityAfterCheck());
            inventoryProducts.add(inventoryProduct);
            inventoryProductRepository.save(inventoryProduct);
        }
        inventory.setInventoryProducts(inventoryProducts);
    }

    public void updateQuantityProduct(CreateInventoryRequest createInventoryRequest, Integer storeId) {
        List<CreateInventoryRequest.Product> products = createInventoryRequest.getProducts();
        for (CreateInventoryRequest.Product product : products) {
            Product product1 = productRepository.findById(product.getProductId()).orElseThrow(()-> {
                throw new NotFoundException("Không tìm thấy sản phẩm có id" + product.getProductId());
            });
            ProductPublic productPublic = ProductPublic.of(product1);

            Integer initialQuantity = productPublic.getQuantity();
            productHistoryService.inventory(product1, product.getQuantityAfterCheck(), initialQuantity);
            product1.setQuantity(product.getQuantityAfterCheck());
            productRepository.save(product1);
        }
    }

    public List<InventoryPublic> getInventoryByStoreIdAndDAy(Integer storeId, LocalDateTime startTime, LocalDateTime endTime) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store với id " + storeId);
        });
        List<Inventory> inventories = inventoryRepository.findAllByStoreIdAAndCreatedAt(store, startTime, endTime);
        return inventories.stream().map(InventoryPublic::of).collect(Collectors.toList());
    }


    public List<InventoryProductPublic> getInventoryProductByStoreIdAndInventoryId(Integer inventoryId) {
        List<InventoryProduct> inventoryProducts = inventoryProductRepository.findAllByInventoryId(inventoryId);
        return inventoryProducts.stream().map(InventoryProductPublic::of).collect(Collectors.toList());
    }
}
