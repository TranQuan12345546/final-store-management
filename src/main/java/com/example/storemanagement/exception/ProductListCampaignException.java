package com.example.storemanagement.exception;

import java.util.List;

public class ProductListCampaignException extends RuntimeException {
    private List<String> productNames;

    public ProductListCampaignException(List<String> productNames) {
        this.productNames = productNames;
    }

    public List<String> getProductNames() {
        return productNames;
    }
}
