package com.example.storemanagement.constant;

public enum Action {
    UPDATE ("Cập nhật tồn kho"),
    CREATE("Tạo mới"),
    SELL("Bán hàng"),
    RETURN("Trả hàng"),

    INVENTORY("Kiểm hàng");


    public String value;
    Action(String value) {
        this.value = value;
    }
}
