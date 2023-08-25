package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.ImagePublic;
import com.example.storemanagement.enity.Image;
import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.StaffRepository;
import com.example.storemanagement.service.ImageService;
import com.example.storemanagement.service.ProductService;
import com.example.storemanagement.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductService productService;
    @Autowired
    private StaffRepository staffRepository;

    @GetMapping("{id}")
    public ResponseEntity<?> readFile(@PathVariable Integer id) {
        Image image = imageService.readFile(id);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(image.getType()))
                .body(image.getData());
    }


    // upload ảnh sản phẩm
    @PostMapping("/product/{productId}")
    public ResponseEntity<?> uploadProductImage(@PathVariable Long productId,
                                        @RequestParam("files") MultipartFile[] files) {
        Product product = productService.getProductById(productId);
        List<ImagePublic> images = imageService.uploadFileProduct(files, product);
        return ResponseEntity.ok(images);
    }




    // Xoá ảnh
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer imageId) {
        imageService.deleteFile(imageId);
        return ResponseEntity.ok("Xoá file thành công");
    }
}
