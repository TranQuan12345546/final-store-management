package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.ImagePublic;
import com.example.storemanagement.enity.Client;
import com.example.storemanagement.enity.Image;
import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.Staff;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.ImageRepository;
import com.example.storemanagement.repository.StaffRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final StaffRepository staffRepository;

    public List<ImagePublic> uploadFileProduct(MultipartFile[] files, Product product) {
        for (MultipartFile file : files) {
            validateFile(file);
        }

        try {
            List<Image> images = new ArrayList<>();
            for (MultipartFile file : files) {
                Image image = Image.builder()
                        .type(file.getContentType())
                        .data(file.getBytes())
                        .product(product)
                        .isAvatar(false)
                        .build();
                images.add(image);
            }
            imageRepository.saveAll(images);
            List<ImagePublic> imagePublics = new ArrayList<>();
            for (Image image : images) {
                imagePublics.add(ImagePublic.of(image));
            }
            return imagePublics;
        } catch (IOException e) {
            throw new RuntimeException("Có lỗi xảy ra");
        }
    }

    public Image uploadAvatar(MultipartFile file) {
        validateFile(file);
        try {
                Image image = Image.builder()
                        .type(file.getContentType())
                        .data(file.getBytes())
                        .isAvatar(true) 
                        .build();
            imageRepository.save(image);
            return image;
        } catch (IOException e) {
            throw new RuntimeException("Có lỗi xảy ra");
        }
    }

    private void validateFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        // Tên file không được trống
        if (fileName == null || fileName.isEmpty()) {
            throw new BadRequestException("Tên file không hợp lệ");
        }

        // Type file có nằm trong ds cho phép hay không
        // avatar.png, image.jpg => png, jpg
        String fileExtension = getFileExtension(fileName);
        if (!checkFileExtension(fileExtension)) {
            throw new BadRequestException("Type file không hợp lệ");
        }

        // Kích thước size có trong phạm vi cho phép không
        double fileSize = (double) (file.getSize() / 1_048_576);
        if (fileSize > 2) {
            throw new BadRequestException("Size file không được vượt quá 2MB");
        }
    }

    public String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex + 1);
    }

    public boolean checkFileExtension(String fileExtension) {
        List<String> fileExtensions = List.of("png", "jpg", "jpeg");
        return fileExtensions.contains(fileExtension);
    }

    public Image readFile(Integer id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException("Not found image");
                });
        return image;
    }

    public void deleteFile(Integer id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> {
                    throw new NotFoundException("Not found image");
                });
        imageRepository.delete(image);
    }

    public List<String> getAllPathImageByProduct(Long productId) {
        List<Image> images = imageRepository.findAllByProductId(productId);
        List<String> imgPaths = new ArrayList<>();
        for (Image image : images) {
            String imgPath = getImgPath(image);
            imgPaths.add(imgPath);
        }
        return imgPaths;
    }

    private String getImgPath(Image image) {
        String imgPath = "http://localhost:8080/" + image.getId();
        return imgPath;
    }


}
