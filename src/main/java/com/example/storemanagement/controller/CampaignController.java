package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.CampaignPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.request.UpsertCampaignRequest;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.service.CampaignService;
import com.example.storemanagement.service.ProductService;
import com.example.storemanagement.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/campaign")
public class CampaignController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private WebService webService;

    @GetMapping("/{storeId}/create-page")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getCreateCampaignPage(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<ProductPublic> productPublics = productService.getAllProductPublicFromStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("productList", productPublics);
            return "web/campaign/create-campaign";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @GetMapping("/{storeId}/all-campaign")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getAllVoucher(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<CampaignPublic> campaignPublics = campaignService.getAllCampaignFromStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("campaignList", campaignPublics);
            return "web/campaign/all-campaign";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @GetMapping("/{storeId}/active")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getVoucherActive(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<CampaignPublic> campaignPublics = campaignService.getAllCampaignActive(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("campaignList", campaignPublics);
            return "web/campaign/campaign-active";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @GetMapping("/{storeId}/expired")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getVoucherExpired(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<CampaignPublic> campaignPublics = campaignService.getAllCampaignExpired(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            return "web/campaign/campaign-expired";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createCampaign(@PathVariable Integer storeId, @RequestBody UpsertCampaignRequest upsertCampaignRequest) {
        CampaignPublic campaignPublic = campaignService.createCampaign(storeId, upsertCampaignRequest);
        return ResponseEntity.ok(campaignPublic);
    }

    @PostMapping("/{storeId}/update")
    public ResponseEntity<?> updateCampaign(@PathVariable Integer storeId, @RequestBody UpsertCampaignRequest upsertCampaignRequest) {
        CampaignPublic campaignPublic = campaignService.updateCampaign(storeId, upsertCampaignRequest);
        return ResponseEntity.ok(campaignPublic);
    }

    @PutMapping("/{storeId}/change-status")
    public ResponseEntity<?> changeStatusCampaign(@PathVariable Integer storeId,
                                                 @RequestParam Integer campaignId,
                                                 @RequestParam Boolean isActive) {
        campaignService.changeActiveCampaign(storeId, campaignId, isActive);
        return ResponseEntity.ok("Thay đổi trạng thái thành công");
    }
}
