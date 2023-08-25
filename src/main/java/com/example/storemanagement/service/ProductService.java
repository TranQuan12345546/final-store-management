package com.example.storemanagement.service;

import com.example.storemanagement.constant.ProductStatus;
import com.example.storemanagement.dto.OverviewResult;
import com.example.storemanagement.dto.ProductDto;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.request.UpsertProductRequest;
import com.example.storemanagement.enity.*;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private TypeProductRepository typeProductRepository;

    @Autowired
    private ProductHistoryService productHistoryService;

    @Autowired
    private ImageService imageService;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ProductDto createProduct(UpsertProductRequest upsertProductRequest, MultipartFile[] files) {
        Product product = new Product();
        Integer storeId = upsertProductRequest.getStore();
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> {
                    throw new NotFoundException("Not found store with id = " + storeId);
                });
        product.setStore(store);
        String codeNumber = upsertProductRequest.getCodeNumber();
        if (codeNumber == null || codeNumber.isEmpty()) {
            codeNumber  = generateUniqueCodeNumberForStore(storeId);
        } else {
            if(productRepository.findByCodeNumberAndStoreId(codeNumber, storeId) != null) {
                throw new RuntimeException("Sản phẩm đã tồn tại trong hệ thống");
            }
        }
        product.setCodeNumber(codeNumber);
        setPropertiesProduct(product, upsertProductRequest);
        productRepository.save(product);
        productHistoryService.createProduct(product);
        if (files != null) {
            imageService.uploadFileProduct(files, product);
        }
        return ProductDto.toProductDto(product);
    }
    private String generateUniqueCodeNumberForStore(Integer storeId) {
        // Tạo mã code mới cho cửa hàng
        Long productCount = productRepository.countByStoreId(storeId);
        String codeNumber = getCodeNumber(productCount);

        while (productRepository.findByCodeNumberAndStoreId(codeNumber, storeId) != null) {
            codeNumber = getCodeNumber(productCount+1);
        }

        return codeNumber;

    }

    static String getCodeNumber(Long quantity) {
        int digits = 6;
        int limit = 999999;

        while (quantity > limit) {
            digits++;
            limit = limit * 10 + 9;
        }
        String format = "";
        if (digits < 10) {
            format = "PR%0" + digits + "d";
        } else {
            format = "PR%" + digits + "d";
        }
        String generatedCodeNumber = String.format(format, quantity + 1);

        return generatedCodeNumber;
    }

    public ProductPublic updateProduct(Long productId, UpsertProductRequest upsertProductRequest) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy sản phẩm có id:" + productId);
        });

        setPropertiesProduct(product, upsertProductRequest);
        productHistoryService.updateProduct(product, upsertProductRequest.getQuantity());
        productRepository.save(product);
        Product product1 = productRepository.findById(productId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy sản phẩm có id:" + productId);
        });

        return ProductPublic.of(product1);
    }

    private void setPropertiesProduct(Product product, UpsertProductRequest upsertProductRequest) {
        product.setName(upsertProductRequest.getName());
        product.setInitialPrice(upsertProductRequest.getInitialPrice());
        product.setSalePrice(upsertProductRequest.getSalePrice());
        Integer userId = upsertProductRequest.getUserId();
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            product.setCreatedBy(user.getFullName());
        }
        Integer inputQuantity = upsertProductRequest.getQuantity();
        product.setQuantity(inputQuantity);
        if(inputQuantity > 0) {
            product.setStatus(ProductStatus.INSTOCK);
        } else {
            product.setStatus(ProductStatus.OUTOFSTOCK);
        }
        product.setDescription(upsertProductRequest.getDescription());
        product.setNote(upsertProductRequest.getNote());
        if (upsertProductRequest.getSupplier() != null) {
            product.setSupplier(upsertProductRequest.getSupplier());
        }
        if (upsertProductRequest.getTypeProduct() != null) {
            product.setTypeProduct(typeProductRepository.findByName(upsertProductRequest.getTypeProduct()));
        }
        product.setGroupProduct(upsertProductRequest.getGroupProduct());

    }



    public Page<ProductDto> getAllProductPage(Integer storeId,Integer groupProductId, Integer page, Integer pageSize) {
        Page<Product> productPage;
        if (groupProductId != 0) {
            productPage = productRepository.findByStoreIdAndGroupProductId(storeId, groupProductId, PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending()));
        } else {
            productPage = productRepository.findByStoreId(storeId, PageRequest.of(page - 1, pageSize, Sort.by("createdAt").descending()));
        }

        List<ProductDto> productDtoList = productPage.getContent()
                .stream()
                .map(ProductDto::toProductDto)
                .collect(Collectors.toList());
        return new PageImpl<>(productDtoList, productPage.getPageable(), productPage.getTotalElements());
    }

    public List<ProductDto> getAllProductDtoFromStore(Integer storeId) {
        List<Product> productList = productRepository.findByStoreId(storeId);
        return productList
                .stream()
                .map(ProductDto::toProductDto)
                .collect(Collectors.toList());
    }


    public List<ProductPublic> getAllProductPublicFromStore(Integer storeId) {
        List<ProductPublic> productPublics = productRepository.findAllByStoreId(storeId);
        return productPublics;
    }

    public ProductPublic getProductDetailById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(()-> {
            throw new NotFoundException("Không tìm thấy sản phẩm với id:" + id);
        });
        return ProductPublic.of(product);
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy sản phẩm với id:" + id);
        });
        return product;
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy sản phẩm với id:" + productId);
        });
        LocalDateTime deleteDayAccepted = LocalDateTime.now().minusDays(30);
        List<Order> orders = orderRepository.findAllByProductId(productId);
        for (Order order : orders) {
            if (order.getOrderDate().isAfter(deleteDayAccepted)) {
                throw new IllegalStateException("Không thể xoá sản phẩm có phát sinh đơn hàng trong vòng 30 ngày");
            }
            order.setProduct(null);
        }
        productRepository.delete(product);

    }

    public List<ProductPublic> getSuggestProduct(String name, Integer storeId) {
        List<ProductPublic> productPublics = new ArrayList<>();
        List<ProductPublic> products = productRepository.findAllByStoreId(storeId);
        for (ProductPublic product : products) {
            String lowercaseProductName = product.getName().toLowerCase();
            String lowercaseProductCode = product.getCodeNumber().toLowerCase();
            if (lowercaseProductName.contains(name) || lowercaseProductCode.contains(name)) {
                productPublics.add(product);
            }
        }
        return productPublics;
    }

    public OverviewResult getOverviewResult(Integer storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> {
            throw new NotFoundException("Không tìm thấy store với id: " + storeId);
        });
        OverviewResult overviewResult = new OverviewResult();

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime startOfDay = today.with(LocalTime.MIN);
        LocalDateTime endOfDay = today.with(LocalTime.MAX);

        // Tính doanh thu trong ngày
        Optional<Long> totalPriceSumToday = orderRepository.getTotalPriceSumForDay(store, startOfDay, endOfDay);

        overviewResult.setRevenue(totalPriceSumToday.orElse(0L));

        // Tính % doanh thu hôm nay so với hôm qua
        Double totalPricePercentToday = totalPriceSumToday.orElse(0L).doubleValue();

        LocalDateTime yesterday = today.minusDays(1);
        LocalDateTime startOfYesterday = yesterday.with(LocalTime.MIN);
        LocalDateTime endOfYesterday = yesterday.with(LocalTime.MAX);

        Optional<Long> totalPriceSumYesterday = orderRepository.getTotalPriceSumForDay(store ,startOfYesterday, endOfYesterday);
        Double totalPricePercentYesterday = totalPriceSumYesterday.orElse(0L).doubleValue();

        double increasePercentage = 0.0;
        if (totalPricePercentYesterday != 0.0) {
            increasePercentage = ((totalPricePercentToday - totalPricePercentYesterday) / totalPricePercentYesterday) * 100.0;
        } else if(totalPricePercentToday == 0.0 && totalPricePercentYesterday == 0.0) {
            increasePercentage = 0.0;
        } else if (totalPricePercentToday != 0.0 && totalPricePercentYesterday == 0.0) {
            increasePercentage = 100.0;
        }

        double roundedPercentage = Math.round(increasePercentage * 100.0) / 100.0;
        overviewResult.setTotalPricePercent(roundedPercentage);

        // tính % số lượng đơn hàng bán ra so với hôm qua
        Optional<Integer> totalOrderToday = orderRepository.getTotalOrderCountForDay(store, startOfDay, endOfDay);
        Optional<Integer> totalOrderYesterday = orderRepository.getTotalOrderCountForDay(store, startOfYesterday, endOfYesterday);
        Double totalOrderPercentToday = totalOrderToday.orElse(0).doubleValue();
        Double totalOrderPercentYesterday = totalOrderYesterday.orElse(0).doubleValue();
        overviewResult.setOrder(totalOrderToday.orElse(0));
        if (totalOrderPercentYesterday != 0.0) {
            overviewResult.setTotalOrderPercent(Math.round(((totalOrderPercentToday - totalOrderPercentYesterday)/totalOrderPercentYesterday * 100) * 100.0) / 100.0);
        } else if (totalOrderPercentToday == 0 && totalOrderPercentYesterday == 0) {
            overviewResult.setTotalOrderPercent(0.0);
        } else {
            overviewResult.setTotalOrderPercent(100.0);
        }

        // Tính tổng giá trị tồn kho
        Long inventory = productRepository.getTotalValueByStoreId(storeId);
        System.out.println(inventory);
        if (inventory != null) {
            overviewResult.setInventoryValue(inventory);
        } else {
            overviewResult.setInventoryValue(0L);
        }

        // Tính tổng số sản phẩm tồn kho
        Integer totalProduct = productRepository.countAvailableProductsByStoreId(storeId);
        if (totalProduct != null) {
            overviewResult.setProduct(totalProduct);
        } else {
            overviewResult.setProduct(0);
        }

        Integer totalCheckStock = inventoryRepository.countByDay(storeId, startOfDay, endOfDay);
        if (totalCheckStock != null) {
            overviewResult.setCheck(totalCheckStock);
        } else {
            overviewResult.setCheck(0);
        }



        return overviewResult;
    }

    public List<ProductPublic> getProductDetailByGroupProduct(Integer storeId, Integer groupId) {
        List<ProductPublic> productPublics = productRepository.findAllByStoreIdAndGroupProductId(storeId, groupId);
        return productPublics;
    }


}
