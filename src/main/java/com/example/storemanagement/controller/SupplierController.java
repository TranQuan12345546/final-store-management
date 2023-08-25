package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.SupplierPublic;
import com.example.storemanagement.dto.request.UpsertSupplierRequest;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.service.SupplierService;
import com.example.storemanagement.service.WebService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private WebService webService;

    // Trang thông tin nhà cung cấp
    @GetMapping("/{storeId}/supplier-info")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getSupplierInfo(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<SupplierPublic> supplierPublics = supplierService.getAllSupplierRelatedStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("supplierList", supplierPublics);
            return "web/supplier/supplier-info";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }
    @Operation(summary = "Thêm nhà cung cấp")
    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> addSupplier(@PathVariable Integer storeId, @Valid @RequestBody UpsertSupplierRequest upsertSupplierRequest) {
        SupplierPublic supplierPublic = supplierService.addSupplier(storeId, upsertSupplierRequest);
        return ResponseEntity.ok(supplierPublic);
    }
}
