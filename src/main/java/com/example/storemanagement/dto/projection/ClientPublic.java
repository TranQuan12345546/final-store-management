package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Client;
import com.example.storemanagement.enity.Image;
import com.example.storemanagement.enity.Order;
import com.example.storemanagement.enity.Staff;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface ClientPublic {
    Integer getId();
    String getName();
    String getAddress();
    String getEmail();
    String getPhone();
    String getBirthday();
    ImagePublic getAvatar();
    LocalDateTime getCreatedAt();
    List<OrderPublic> getOrder();
    @RequiredArgsConstructor
    class ClientPublicImpl implements ClientPublic {
        private final Client client;

        @Override
        public Integer getId() {
            return this.client.getId();
        }

        @Override
        public String getName() {
            return this.client.getName();
        }

        @Override
        public String getAddress() {
            return this.client.getAddress();
        }

        @Override
        public String getEmail() {
            return this.client.getEmail();
        }

        @Override
        public String getPhone() {
            return this.client.getPhone();
        }

        @Override
        public String getBirthday() {
            return this.client.getBirthday();
        }

        @Override
        public ImagePublic getAvatar() {
            if (this.client.getAvatar() == null) {
                return null;
            }
            return ImagePublic.of(this.client.getAvatar());
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return this.client.getCreatedAt();
        }

        @Override
        public List<OrderPublic> getOrder() {
            if(this.client.getOrders() == null) {
                return null;
            }
            List<OrderPublic> orderPublics = new ArrayList<>();
            for (Order order : this.client.getOrders()) {
                orderPublics.add(OrderPublic.of(order));
            }
            return orderPublics;
        }
    }
    static ClientPublic of(Client client) {
        return new ClientPublicImpl(client);
    }
}
