package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Order;
import com.example.storemanagement.enity.Owner;
import com.example.storemanagement.enity.Staff;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface StaffPublic {
    Integer getId();
    String getUsername();
    String getFullName();
    String getAddress();
    String getEmail();
    String getPhone();
    String getBirthday();
    LocalDateTime getCreatedAt();
    Boolean getStatus();
    ImagePublic getAvatar();
    String getStoreName();

    @RequiredArgsConstructor
    class StaffPublicImpl implements StaffPublic {
        private final Staff staff;
        @Override
        public Integer getId() {
            return this.staff.getId();
        }

        @Override
        public String getUsername() {
            return this.staff.getUsername();
        }

        @Override
        public String getFullName() {
            return this.staff.getFullName();
        }

        @Override
        public String getAddress() {
            return this.staff.getAddress();
        }

        @Override
        public String getEmail() {
            return this.staff.getEmail();
        }

        @Override
        public String getPhone() {
            return this.staff.getPhone();
        }

        @Override
        public String getBirthday() {
            return this.staff.getBirthday();
        }

        @Override
        public LocalDateTime getCreatedAt() {
            return this.staff.getCreatedAt();
        }

        @Override
        public Boolean getStatus() {
            if (this.staff.getStatus() == null) {
                return null;
            }
            return this.staff.getStatus();
        }

        @Override
        public ImagePublic getAvatar() {
            if (this.staff.getAvatar() == null) {
                return null;
            }
            return ImagePublic.of(this.staff.getAvatar());
        }

        @Override
        public String getStoreName() {
            return this.staff.getStore().getName();
        }
    }
    static StaffPublic of(Staff staff) {
        return new StaffPublicImpl(staff);
    }
}
