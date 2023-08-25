package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.*;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface OwnerPublic {
    Integer getId();
    String getUsername();
    String getAddress();
    String getEmail();
    String getPhone();
    LocalDateTime getCreatedAt();
    Boolean getStatus();
    ImagePublic getAvatar();
    List<StorePublic> getStores();
    @RequiredArgsConstructor
    class OwnerPublicImpl implements OwnerPublic {
        private final Owner owner;

        @Override
        public Integer getId() {
            return this.owner.getId();
        }

        @Override
        public String getUsername() {
            return this.owner.getUsername();
        }

        @Override
        public String getAddress() {
            return this.owner.getAddress();
        }

        @Override
        public String getEmail() {
            return this.owner.getEmail();
        }

        @Override
        public String getPhone() {
            return this.owner.getPhone();
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return this.owner.getCreatedAt();
        }

        @Override
        public Boolean getStatus() {
            if (this.owner.getStatus() == null) {
                return null;
            }
            return this.owner.getStatus();
        }

        @Override
        public ImagePublic getAvatar() {
            if(this.owner.getAvatar() == null) {
                return null;
            }
            return ImagePublic.of(this.owner.getAvatar());
        }

        @Override
        public List<StorePublic> getStores() {
            List<StorePublic> storePublics = new ArrayList<>();
            for (Store store : this.owner.getStores()) {
                storePublics.add(StorePublic.of(store));
            }
            return storePublics;
        }

    }
    static OwnerPublic of(Owner owner) {
        return new OwnerPublicImpl(owner);
    }
}
