package com.example.storemanagement.service;

import com.example.storemanagement.constant.ReduceType;
import com.example.storemanagement.dto.projection.VoucherPublic;
import com.example.storemanagement.dto.request.UpsertVoucherRequest;
import com.example.storemanagement.enity.Product;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.Voucher;
import com.example.storemanagement.exception.CheckVoucherException;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.ProductRepository;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class VoucherService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private StoreRepository storeRepository;

    public VoucherPublic createVoucher(Integer storeId, UpsertVoucherRequest upsertVoucherRequest) {
        Voucher voucher = new Voucher();
        String code = upsertVoucherRequest.getCode();
        if (code == null || code.isEmpty()) {
            code = generateUniqueCodeForStore(8);
        } else {
            if(voucherRepository.findByCodeAndStoreId(code, storeId) != null) {
                throw new RuntimeException("Voucher đã tồn tại trong hệ thống");
            }
        }
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store");
        });
        voucher.setStore(store);
        voucher.setCode(code);
        voucher.setTitle(upsertVoucherRequest.getTitle());
        voucher.setReducedPrice(upsertVoucherRequest.getReducedPrice());
        voucher.setOriginalQuantity(upsertVoucherRequest.getOriginalQuantity());
        voucher.setQuantityPerClient(upsertVoucherRequest.getQuantityPerClient());
        if (upsertVoucherRequest.getReduceType() == 1) {
            voucher.setReduceType(ReduceType.BY_PERCENT);
        } else {
            voucher.setReduceType(ReduceType.BY_PRICE);
        }
        voucher.setStartDate(upsertVoucherRequest.getStartDate().with(LocalTime.MIN));
        voucher.setEndDate(upsertVoucherRequest.getEndDate().with(LocalTime.MAX));
        List<Product> productList = productRepository.findAllById(upsertVoucherRequest.getProductId());
        voucher.setProducts(productList);
        voucherRepository.save(voucher);
        return VoucherPublic.of(voucher);
    }

    private String generateUniqueCodeForStore(Integer storeId) {
        // Tạo mã code mới cho cửa hàng
        String code = generateRandomUpperCaseString(8);

        while (voucherRepository.findByCodeAndStoreId(code, storeId) != null) {
            code = generateRandomUpperCaseString(8);
        }
        return code;

    }

    private String generateRandomUpperCaseString(int length) {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder result = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charset.length());
            result.append(charset.charAt(randomIndex));
        }

        return result.toString();
    }

    public VoucherPublic updateVoucher(Integer storeId, UpsertVoucherRequest upsertVoucherRequest) {
        Voucher voucher = voucherRepository.findById(upsertVoucherRequest.getVoucherId()).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy voucher");
        });
        voucher.setTitle(upsertVoucherRequest.getTitle());
        voucher.setCode(upsertVoucherRequest.getCode());
        voucher.setStatus(upsertVoucherRequest.getStatus());
        if (upsertVoucherRequest.getReduceType() == 1) {
            voucher.setReduceType(ReduceType.BY_PERCENT);
        } else {
            voucher.setReduceType(ReduceType.BY_PRICE);
        }
        voucher.setReducedPrice(upsertVoucherRequest.getReducedPrice());
        voucher.setQuantityPerClient(upsertVoucherRequest.getQuantityPerClient());
        voucher.setStartDate(upsertVoucherRequest.getStartDate().with(LocalTime.MIN));
        voucher.setEndDate(upsertVoucherRequest.getEndDate().with(LocalTime.MAX));
        voucherRepository.save(voucher);
        return VoucherPublic.of(voucher);
    }

    public List<VoucherPublic> getAllVoucherNow(Integer storeId) {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> vouchers = voucherRepository.findALlVoucherByTime(storeId, now);
        return vouchers.stream().map(VoucherPublic::of).collect(Collectors.toList());
    }

    public List<VoucherPublic> getAllVoucherFromStore(Integer storeId) {
        List<Voucher> vouchers = voucherRepository.findAllByStoreId(storeId);
        return vouchers.stream().map(VoucherPublic::of).collect(Collectors.toList());
    }

    public VoucherPublic checkVoucherExpired(Integer storeId, String code) {
        Voucher voucher = voucherRepository.findByCodeAndStoreId(code, storeId);
        if (voucher == null) {
            throw new NotFoundException("Không tìm thấy voucher");
        }
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getOriginalQuantity() - voucher.getUsedQuantity() == 0) {
            throw new CheckVoucherException("Voucher này đã hết lượt sử dụng");
        }
        if (now.isBefore(voucher.getStartDate())) {
            throw new CheckVoucherException("Voucher này chưa có hiệu lực.");
        }
        if (now.isAfter(voucher.getEndDate())) {
            throw new CheckVoucherException("Voucher này đã hết hiệu lực.");
        }
        return VoucherPublic.of(voucher);
    }

    public void changeStatusVoucher(Integer storeId, Integer voucherId, Boolean status) {
        Voucher voucher = voucherRepository.findById(voucherId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy voucher");
        });
        voucher.setStatus(status);
        voucherRepository.save(voucher);
    }


}
