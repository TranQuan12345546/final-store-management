package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Campaign;
import com.example.storemanagement.enity.Product;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface CampaignPublic {
    Integer getId();
    String getTitle();
    Integer getReducedPrice();
    String getReduceType();
    Boolean getIsActive();

    LocalDateTime getStartDate();

    LocalDateTime getEndDate();
    List<ProductPublic> getProducts();

    @RequiredArgsConstructor
    class CampaignPublicImpl implements CampaignPublic {
        private final Campaign campaign;

        @Override
        public Integer getId() {
            return this.campaign.getId();
        }

        @Override
        public String getTitle() {
            return this.campaign.getTitle();
        }

        @Override
        public Integer getReducedPrice() {
            return this.campaign.getReducedPrice();
        }

        @Override
        public String getReduceType() {
            return this.campaign.getReduceType().value;
        }

        @Override
        public Boolean getIsActive() {
            return this.campaign.getIsActive();
        }

        @Override
        public LocalDateTime getStartDate() {
            return this.campaign.getStartDate();
        }

        @Override
        public LocalDateTime getEndDate() {
            return this.campaign.getEndDate();
        }

        @Override
        public List<ProductPublic> getProducts() {
            List<ProductPublic> products = new ArrayList<>();
            for (Product product : this.campaign.getProducts()) {
                products.add(ProductPublic.of(product));
            }
            return products;
        }
    }

    static CampaignPublic of(Campaign campaign) {
        return new CampaignPublicImpl(campaign);
    }
}
