package com.example.storemanagement.constant;

public enum ProductStatus {
    INSTOCK("Đang kinh doanh"),
    OUTOFSTOCK("Hết hàng");

    public String value;
    ProductStatus(String value) {
        this.value = value;
    }
}
