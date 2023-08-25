package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.ClientPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.projection.StaffPublic;
import com.example.storemanagement.dto.request.UpsertClientRequest;
import com.example.storemanagement.dto.request.UpsertStaffRequest;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.repository.StaffRepository;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.UserRepository;
import com.example.storemanagement.service.ClientService;
import com.example.storemanagement.service.WebService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private WebService webService;

    // trang thông tin khách hàng
    @GetMapping("/{storeId}")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getCustomerInfo(@PathVariable Integer storeId, Model model, Authentication authentication) {
       boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<ClientPublic> clientPublics = clientService.getAllClientFromStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("clientList", clientPublics);
            return "web/client/client-info";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }

    }

    // Tạo khách hàng mới
    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createNewStaff(@PathVariable Integer storeId,
                                            @RequestParam("fullName") @NotBlank String fullName,
                                            @RequestParam("email") @NotBlank String email,
                                            @RequestParam(value = "address", required = false) String address,
                                            @RequestParam(value = "phone", required = false) String phone,
                                            @RequestParam("birthday") String birthday,
                                            @RequestParam(value = "file", required = false) MultipartFile file) {
        UpsertClientRequest upsertClientRequest = UpsertClientRequest.builder()
                .fullName(fullName)
                .email(email)
                .address(address)
                .phone(phone)
                .birthday(birthday)
                .build();
        ClientPublic clientPublic = clientService.createNewClient(storeId, upsertClientRequest , file);
        return ResponseEntity.ok(clientPublic);
    }


    // Upload avatar client
    @PostMapping("/avatar/{clientId}")
    public ResponseEntity<?> uploadAvatarImage(@PathVariable Integer clientId,
                                               @RequestParam("file") MultipartFile file) {
        clientService.uploadAvatarImage(file, clientId);
        return ResponseEntity.ok("upload thành công");
    }

    // Tìm kiếm khách hàng
    @GetMapping("/{storeId}/suggest")
    public ResponseEntity<?> getSuggestClient(@RequestParam String name, @PathVariable Integer storeId) {
        List<ClientPublic> clientPublics = clientService.getSuggestClient(name, storeId);
        return ResponseEntity.ok(clientPublics);
    }

    // Update thông tin khách hàng
    @PutMapping("/update")
    public ResponseEntity<?> updateStaffInfo(@RequestBody UpsertClientRequest upsertClientRequest) {
        ClientPublic clientPublic = clientService.updateClientInfo(upsertClientRequest);
        return ResponseEntity.ok(clientPublic);
    }

    @DeleteMapping("/delete/{clientId}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.ok("Delete successful.");
    }

}
