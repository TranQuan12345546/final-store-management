package com.example.storemanagement.service;

import com.example.storemanagement.constant.ReduceType;
import com.example.storemanagement.dto.projection.CampaignPublic;
import com.example.storemanagement.dto.request.UpsertCampaignRequest;
import com.example.storemanagement.enity.Campaign;
import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.exception.ProductListCampaignException;
import com.example.storemanagement.repository.CampaignRepository;
import com.example.storemanagement.repository.ProductRepository;
import com.example.storemanagement.repository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CampaignService {
    private static final Logger logger = LoggerFactory.getLogger(CampaignService.class);
    private final CampaignRepository campaignRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public CampaignService(CampaignRepository campaignRepository,
                           StoreRepository storeRepository,
                           ProductRepository productRepository) {
        this.campaignRepository = campaignRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    public List<CampaignPublic> getAllCampaignFromStore(Integer storeId) {
        List<Campaign> campaigns = campaignRepository.findAllByStoreId(storeId);
        return campaigns.stream().map(CampaignPublic::of).collect(Collectors.toList());
    }

    public List<CampaignPublic> getAllCampaignActive(Integer storeId) {
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> campaigns = campaignRepository.findAllCampaignRunning(storeId, now);
        return campaigns.stream().map(CampaignPublic::of).collect(Collectors.toList());
    }

    public CampaignPublic createCampaign(Integer storeId, UpsertCampaignRequest upsertCampaignRequest) {
        Campaign campaign = new Campaign();
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store");
        });
        campaign.setStore(store);
        campaign.setTitle(upsertCampaignRequest.getTitle());
        campaign.setReducedPrice(upsertCampaignRequest.getReducedPrice());
        if (upsertCampaignRequest.getReduceType() == 1) {
            campaign.setReduceType(ReduceType.BY_PERCENT);
        } else {
            campaign.setReduceType(ReduceType.BY_PRICE);
        }
        campaign.setStartDate(upsertCampaignRequest.getStartDate().with(LocalTime.MIN));
        campaign.setEndDate(upsertCampaignRequest.getEndDate().with(LocalTime.MAX));
        List<Product> productList = productRepository.findAllById(upsertCampaignRequest.getProductId());
        List<String> productNames = new ArrayList<>();
        for (Product product : productList) {
            List<Campaign> campaigns = campaignRepository.findConflictingCampaigns(product, campaign.getStartDate(), campaign.getEndDate());
            if (campaigns.size() != 0) {
                productNames.add(product.getName());
            }
        }
        if (productNames.size() != 0) {
            throw new ProductListCampaignException(productNames);
        }
        campaign.setProducts(productList);
        campaignRepository.save(campaign);
        return CampaignPublic.of(campaign);
    }

    public CampaignPublic updateCampaign(Integer storeId, UpsertCampaignRequest upsertCampaignRequest) {
        Campaign campaign = campaignRepository.findById(upsertCampaignRequest.getCampaignId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy chiến dịch");
        });
        campaign.setTitle(upsertCampaignRequest.getTitle());
        campaign.setReducedPrice(upsertCampaignRequest.getReducedPrice());
        if (upsertCampaignRequest.getReduceType() == 1) {
            campaign.setReduceType(ReduceType.BY_PERCENT);
        } else {
            campaign.setReduceType(ReduceType.BY_PRICE);
        }
        campaign.setStartDate(upsertCampaignRequest.getStartDate().with(LocalTime.MIN));
        campaign.setEndDate(upsertCampaignRequest.getEndDate().with(LocalTime.MAX));
        campaign.setIsActive(upsertCampaignRequest.getIsActive());
        campaignRepository.save(campaign);
        return CampaignPublic.of(campaign);
    }

    public void changeActiveCampaign(Integer storeId, Integer campaignId, Boolean isActive) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy chiến dịch");
        });
        campaign.setIsActive(isActive);
        for (Product product : campaign.getProducts()) {
            product.setIsOnPromotional(isActive);
        }
        campaignRepository.save(campaign);
    }

    public List<CampaignPublic> getAllCampaignExpired(Integer storeId) {
        LocalDateTime now = LocalDateTime.now();
        List<Campaign> campaigns = campaignRepository.findAllCampaignExpired(storeId, now);
        return campaigns.stream().map(CampaignPublic::of).collect(Collectors.toList());
    }

    public void runPromotions(Integer storeId) {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        List<Campaign> campaigns = campaignRepository.findAllCampaignByStartDate(storeId, today);
        if (!campaigns.isEmpty()) {
            for (Campaign campaign : campaigns) {
                if (campaign.getReduceType() == ReduceType.BY_PERCENT) {
                    Double promotionalPrice = 1 - (campaign.getReducedPrice()/100.0);
                    for (Product product : campaign.getProducts()) {
                        double promotionalPriceDouble = product.getSalePrice() * promotionalPrice;
                        int roundedPromotionalPrice = (int) Math.round(promotionalPriceDouble);

                        product.setPromotionalPrice(roundedPromotionalPrice);
                        product.setIsOnPromotional(true);
                        productRepository.save(product);
                    }
                } else if (campaign.getReduceType() == ReduceType.BY_PRICE) {
                    Integer promotionalPrice = campaign.getReducedPrice();
                    for (Product product : campaign.getProducts()) {
                        product.setPromotionalPrice(product.getSalePrice() - promotionalPrice);
                        product.setIsOnPromotional(true);
                        productRepository.save(product);
                    }
                }
                logger.info("Chương trình khuyến mãi " + campaign.getTitle() + " được chạy") ;
            }
        }
    }


    public void endPromotions(Integer storeId) {
        LocalDateTime today = LocalDateTime.now().with(LocalTime.MAX);
        List<Campaign> campaigns = campaignRepository.findAllCampaignByEndDate(storeId, today);
        if (!campaigns.isEmpty()) {
            for (Campaign campaign : campaigns) {
                for (Product product : campaign.getProducts()) {
                    product.setPromotionalPrice(0);
                    product.setIsOnPromotional(false);
                    productRepository.save(product);
                }
                logger.info("Chương trình khuyến mãi " + campaign.getTitle() + " đã dừng");
            }
        }
    }


}
