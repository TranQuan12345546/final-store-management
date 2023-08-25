package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Order;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

public interface OrderPublic {
    Long getId();
    String getOrderNumber();
    String getStoreName();
    String getCreatedBy();
    String getClientName();
    String getProductName();
    String getGroupProductName();
    Integer getQuantity();
    Integer getDiscount();
    Long getTotalPrice();
    LocalDateTime getOrderDate();

    Boolean getIsReturn();

    String getReturnCreatedBy();

    LocalDateTime getReturnCreatedAt();
    @RequiredArgsConstructor
    class OrderPublicImpl implements OrderPublic {
        private final Order order;

        @Override
        public Long getId() {
            return this.order.getId();
        }

        @Override
        public String getOrderNumber() {
            return this.order.getOrderNumber();
        }

        @Override
        public String getStoreName() {
            return this.order.getStore().getName();
        }

        @Override
        public String getCreatedBy() {
            if (this.order.getUser() != null) {
                return this.order.getUser().getFullName();
            } else {
                return null;
            }
        }

        @Override
        public String getClientName() {
            if (this.order.getClient() == null) {
                return "Khách lẻ";
            }
            return this.order.getClient().getName();
        }

        @Override
        public String getProductName() {
            return this.order.getProductName();
        }

        @Override
        public String getGroupProductName() {
            return this.order.getGroupProductName();
        }

        @Override
        public Integer getQuantity() {
            return this.order.getQuantity();
        }

        @Override
        public Integer getDiscount() {
            return this.order.getDiscount();
        }

        @Override
        public Long getTotalPrice() {
            return this.order.getTotalPrice();
        }
        @Override
        public LocalDateTime getOrderDate() {
            return this.order.getOrderDate();
        }

        @Override
        public Boolean getIsReturn() {
            if (this.order.getIsReturn() == true) {
                return true;
            }
            else return false;
        }

        @Override
        public String getReturnCreatedBy() {
            return this.order.getReturnCreatedBy();
        }

        @Override
        public LocalDateTime getReturnCreatedAt() {
            return this.order.getReturnCreatedAt();
        }

    }

    static OrderPublic of(Order order) {
        return new OrderPublicImpl(order);
    }
}
