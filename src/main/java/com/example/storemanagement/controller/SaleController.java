package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.CampaignPublic;
import com.example.storemanagement.dto.projection.GroupProductPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Controller
public class SaleController {
    @Autowired
    private ProductService productService;

    @Autowired
    private GroupProductService groupProductService;


    @Autowired
    private CampaignService campaignService;
    @Autowired
    private WebService webService;

    // trang bán hàng
    @GetMapping("/sale/{storeId}")
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_STAFF')")
    public String getSalePage(Model model, @PathVariable Integer storeId, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<ProductPublic> productPublicList = productService.getAllProductPublicFromStore(storeId);
            List<GroupProductPublic> groupProductList = groupProductService.getAllGroupProductByStore(storeId);
            List<CampaignPublic> campaignPublics = campaignService.getAllCampaignActive(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("productList", productPublicList);
            model.addAttribute("groupProductList", groupProductList);
            model.addAttribute("campaignList", campaignPublics);
            return "web/sale";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

}
