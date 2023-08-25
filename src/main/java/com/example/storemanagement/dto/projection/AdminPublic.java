package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Admin;
import lombok.RequiredArgsConstructor;

public interface AdminPublic {
    Integer getId();
    String getFullName();
    String getUsername();
    String getAddress();
    String getEmail();
    String getPhone();
    ImagePublic getAvatar();

    @RequiredArgsConstructor
    class AdminPublicImpl implements AdminPublic {
        private final Admin admin;

        @Override
        public Integer getId() {
            return this.admin.getId();
        }

        @Override
        public String getFullName() {
            return this.admin.getFullName();
        }

        @Override
        public String getUsername() {
            return this.admin.getUsername();
        }

        @Override
        public String getAddress() {
            return this.admin.getAddress();
        }

        @Override
        public String getEmail() {
            return this.admin.getEmail();
        }

        @Override
        public String getPhone() {
            return this.admin.getPhone();
        }

        @Override
        public ImagePublic getAvatar() {
            if (this.admin.getAvatar() == null) {
                return null;
            }
            return ImagePublic.of(this.admin.getAvatar());
        }
    }

    static AdminPublic of(Admin admin) {
        return new AdminPublicImpl(admin);
    }
}
