package com.example.storemanagement.constant;

public enum Status {
    NOT_VALID("Chưa có hiệu lực "),
    RUNNING("Đang chạy"),
    EXPIRED("Đã kết thúc");

    public String value;
    Status(String value) {
        this.value = value;
    }
}
