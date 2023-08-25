package com.example.storemanagement.constant;

public enum ReduceType {
    BY_PERCENT("Theo phần trăm"),
    BY_PRICE("Theo giá thành");

    public String value;
    ReduceType(String value) {
        this.value = value;
    }
}
