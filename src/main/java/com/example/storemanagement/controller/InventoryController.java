package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.GroupProductPublic;
import com.example.storemanagement.dto.projection.InventoryProductPublic;
import com.example.storemanagement.dto.projection.InventoryPublic;
import com.example.storemanagement.dto.request.CreateInventoryRequest;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.service.GroupProductService;
import com.example.storemanagement.service.InventoryService;
import com.example.storemanagement.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Controller
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private GroupProductService groupProductService;
    @Autowired
    private WebService webService;

    // Trang kiểm hàng
    @GetMapping("/{storeId}/check-stock")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getCheckStock(@PathVariable Integer storeId, Model model, Authentication authentication) {
       boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<GroupProductPublic> groupProductList = groupProductService.getAllGroupProductByStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("groupProductList", groupProductList);
            return "web/inventory/check-stock";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    // Trang lịch sử kiểm hàng
    @GetMapping("/{storeId}/check-stock/history")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getCheckStockHistory(@PathVariable Integer storeId, Model model, Authentication authentication) {
       boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<GroupProductPublic> groupProductList = groupProductService.getAllGroupProductByStore(storeId);
            LocalDateTime today = LocalDateTime.now();
            LocalDateTime startOfDay = today.with(LocalTime.MIN);
            LocalDateTime endOfDay = today.with(LocalTime.MAX);
            List<InventoryPublic> inventoryPublics = inventoryService.getInventoryByStoreIdAndDAy(storeId, startOfDay, endOfDay);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("groupProductList", groupProductList);
            model.addAttribute("inventoryList", inventoryPublics);
            return "web/inventory/check-stock-history";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }


    //Lấy thông tin lịch sử kiểm hàng
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getInventoryByStoreId(@PathVariable Integer storeId,
                                                   @RequestParam("startDate") String startDate,
                                                   @RequestParam("endDate") String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDateParsed = LocalDateTime.parse(startDate, formatter);
        LocalDateTime endDateParsed = LocalDateTime.parse(endDate, formatter);
        List<InventoryPublic> inventoryPublics = inventoryService.getInventoryByStoreIdAndDAy(storeId, startDateParsed, endDateParsed);
        return ResponseEntity.ok(inventoryPublics);
    }

    //Lấy thông tin InventoryProduct ứng với từng Inventory
    @GetMapping("/{storeId}/{inventoryId}/inventory-product")
    public ResponseEntity<?> getInventoryProductByStoreIdAndInventoryId(@PathVariable Integer storeId, @PathVariable Integer inventoryId) {
        List<InventoryProductPublic> inventoryProductPublics = inventoryService.getInventoryProductByStoreIdAndInventoryId(inventoryId);
        return ResponseEntity.ok(inventoryProductPublics);
    }

    // Tạo bản ghi inventory
    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createInventory(@PathVariable Integer storeId,
                                                   @RequestBody CreateInventoryRequest createInventoryRequest) {
        inventoryService.createInventory(createInventoryRequest, storeId);
        return ResponseEntity.ok("Update thành công");
    }

    // Cập nhật số lượng sản phẩm
    @PutMapping("/{storeId}/update-quantity")
    public ResponseEntity<?> updateQuantityProduct(@PathVariable Integer storeId,
                                                   @RequestBody CreateInventoryRequest createInventoryRequest) {
        inventoryService.updateQuantityProduct(createInventoryRequest, storeId);
        return ResponseEntity.ok("Update thành công");
    }
}
