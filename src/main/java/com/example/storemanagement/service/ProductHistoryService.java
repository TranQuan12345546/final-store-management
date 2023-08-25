package com.example.storemanagement.service;

import com.example.storemanagement.constant.Action;
import com.example.storemanagement.dto.projection.ProductHistoryPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.ProductHistory;
import com.example.storemanagement.repository.ProductHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductHistoryService {

    @Autowired
    private ProductHistoryRepository productHistoryRepository;

    public void createProduct (Product product) {
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProduct(product);
        productHistory.setInitialPrice(product.getInitialPrice());
        productHistory.setAction(Action.CREATE.value);
        productHistory.setChangeQuantity(product.getQuantity());
        productHistory.setFinalQuantity(product.getQuantity());
        productHistory.setCreatedBy(product.getCreatedBy());
        productHistoryRepository.save(productHistory);
    }

    public void updateProduct(Product product, Integer inputQuantity) {
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProduct(product);
        productHistory.setInitialPrice(product.getInitialPrice());
        productHistory.setAction(Action.UPDATE.value);
        Integer changeQuantity = inputQuantity - product.getQuantity();
        productHistory.setChangeQuantity(changeQuantity);
        productHistory.setFinalQuantity(inputQuantity);
        productHistory.setCreatedBy(product.getCreatedBy());
        productHistoryRepository.save(productHistory);
    }

    public void inventory(Product product, Integer inputQuantity, Integer initialQuantity) {
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProduct(product);
        productHistory.setInitialPrice(product.getInitialPrice());
        productHistory.setAction(Action.INVENTORY.value);
        Integer changeQuantity = inputQuantity - initialQuantity;
        productHistory.setChangeQuantity(changeQuantity);
        productHistory.setFinalQuantity(inputQuantity);
        productHistory.setCreatedBy(product.getCreatedBy());
        productHistoryRepository.save(productHistory);
    }

    public void saleProduct(Product product, String createBy, Integer changeQuantity) {
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProduct(product);
        productHistory.setInitialPrice(product.getInitialPrice());
        productHistory.setAction(Action.SELL.value);
        productHistory.setChangeQuantity(-changeQuantity);
        productHistory.setFinalQuantity(product.getQuantity());
        productHistory.setCreatedBy(createBy);
        productHistoryRepository.save(productHistory);
    }

    public void returnProduct(Product product, String createBy, Integer changeQuantity) {
        ProductHistory productHistory = new ProductHistory();
        productHistory.setProduct(product);
        productHistory.setInitialPrice(product.getInitialPrice());
        productHistory.setAction(Action.RETURN.value);
        productHistory.setChangeQuantity(changeQuantity);
        productHistory.setFinalQuantity(product.getQuantity());
        productHistory.setCreatedBy(createBy);
        productHistoryRepository.save(productHistory);
    }


    public List<ProductHistoryPublic> getProductHistory(Long productId) {
        List<ProductHistory> productHistoryList = productHistoryRepository.findAllByProductId(productId, Sort.by("updatedAt").descending());
        return productHistoryList.stream().map(ProductHistoryPublic::of).collect(Collectors.toList());
    }
}
