package com.example.storemanagement.service;

import com.example.storemanagement.enity.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ScheduledTasks {

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private StoreService storeService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Bangkok")
    public void runPromotion() {
        List<Store> stores = storeService.getAllStore();
        for (Store store : stores) {
            campaignService.runPromotions(store.getId());
        }

    }

    @Scheduled(cron = "59 59 23 * * ?", zone = "Asia/Bangkok")
    public void endPromotion() {
        List<Store> stores = storeService.getAllStore();
        for (Store store : stores) {
            campaignService.endPromotions(store.getId());
        }
    }




}

