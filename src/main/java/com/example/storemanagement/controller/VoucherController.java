package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.projection.VoucherPublic;
import com.example.storemanagement.dto.request.UpsertVoucherRequest;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.service.ProductService;
import com.example.storemanagement.service.VoucherService;
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
@RequestMapping("/voucher")
public class VoucherController {
    @Autowired
    private VoucherService voucherService;

    @Autowired
    private ProductService productService;
    @Autowired
    private WebService webService;

    @GetMapping("/{storeId}/create-page")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getCreateVoucherPage(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<ProductPublic> productPublics = productService.getAllProductPublicFromStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("productList", productPublics);
            return "web/voucher/create-voucher";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @GetMapping("/{storeId}/all-voucher")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getAllVoucher(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<VoucherPublic> voucherPublics = voucherService.getAllVoucherFromStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("voucherList", voucherPublics);
            return "web/voucher/all-voucher";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @GetMapping("/{storeId}/active")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getVoucherActive(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<VoucherPublic> voucherPublics = voucherService.getAllVoucherNow(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("voucherList", voucherPublics);
            return "web/voucher/voucher-active";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @GetMapping("/{storeId}/expired")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getVoucherExpired(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<VoucherPublic> voucherPublics = voucherService.getAllVoucherNow(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("voucherList", voucherPublics);
            return "web/voucher/voucher-expired";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createVoucher(@PathVariable Integer storeId, @RequestBody UpsertVoucherRequest upsertVoucherRequest) {
        VoucherPublic voucherPublic = voucherService.createVoucher(storeId, upsertVoucherRequest);
        return ResponseEntity.ok(voucherPublic);
    }

    @PutMapping("/{storeId}/update")
    public ResponseEntity<?> updateVoucher(@PathVariable Integer storeId, @RequestBody UpsertVoucherRequest upsertVoucherRequest) {
        VoucherPublic voucherPublic = voucherService.updateVoucher(storeId, upsertVoucherRequest);
        return ResponseEntity.ok(voucherPublic);
    }


    // Kiểm tra voucher được nhập vào
    @GetMapping("/{storeId}/check-expired")
    public ResponseEntity<?> checkVoucherExpired(@PathVariable Integer storeId, @RequestParam String code) {
        VoucherPublic voucherPublic = voucherService.checkVoucherExpired(storeId, code);
        return ResponseEntity.ok(voucherPublic);
    }

    // Kích hoạt/huỷ kích hoạt voucher
    @PutMapping("/{storeId}/change-status")
    public ResponseEntity<?> changeStatusVoucher(@PathVariable Integer storeId,
                                                 @RequestParam Integer voucherId,
                                                 @RequestParam Boolean status) {
        voucherService.changeStatusVoucher(storeId, voucherId, status);
        return ResponseEntity.ok("Thay đổi trạng thái thành công");
    }



}
