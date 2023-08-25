package com.example.storemanagement.controller;

import com.example.storemanagement.dto.OverviewResult;
import com.example.storemanagement.dto.ProductDto;
import com.example.storemanagement.dto.projection.GroupProductPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.projection.SupplierPublic;
import com.example.storemanagement.dto.request.UpsertProductRequest;
import com.example.storemanagement.enity.GroupProduct;
import com.example.storemanagement.enity.Supplier;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.repository.ProductRepository;
import com.example.storemanagement.service.*;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private GroupProductService groupProductService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ExcelService excelService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WebService webService;

    // Trang tổng quan
    @GetMapping("/{storeId}/overview")
    @PreAuthorize("hasAnyRole('ROLE_OWNER', 'ROLE_STAFF')")
    public String getOverview(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            OverviewResult overviewResult = productService.getOverviewResult(storeId);
            model.addAttribute("overview", overviewResult);
            model.addAttribute("storeId", storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            return "web/overview";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    //Trang inventory
    @GetMapping("/{storeId}/inventory")
    @PreAuthorize("hasAnyRole('ROLE_OWNER', 'ROLE_STAFF')")
    public String getInventory(@RequestParam(required = false, defaultValue = "1") Integer page,
                               @RequestParam(required = false, defaultValue = "9") Integer pageSize,
                               @RequestParam(required = false, defaultValue = "0") Integer groupProductId,
                               @PathVariable Integer storeId,
                               Model model,
                               Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            Page<ProductDto> productDtoPage = productService.getAllProductPage(storeId,groupProductId, page, pageSize);
            List<GroupProductPublic> groupProductList = groupProductService.getAllGroupProductByStore(storeId);
            List<SupplierPublic> supplierList = supplierService.getAllSupplierRelatedStore(storeId);
            List<ProductDto> productDtoList = productService.getAllProductDtoFromStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("page", productDtoPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("groupProductList", groupProductList);
            model.addAttribute("supplierList", supplierList);
            model.addAttribute("storeId", storeId);
            model.addAttribute("productList", productDtoList);
            return "web/products/inventory";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }

    }

    // Trang thẻ kho
    @GetMapping("/{storeId}/stock-card")
    @PreAuthorize("hasAnyRole('ROLE_OWNER', 'ROLE_STAFF')")
    public String getStockCard(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<ProductPublic> productPublics = productService.getAllProductPublicFromStore(storeId);
            List<GroupProductPublic> groupProductList = groupProductService.getAllGroupProductByStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("productList", productPublics);
            model.addAttribute("groupProductList", groupProductList);
            return "web/products/stock-card";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }




    // Danh sách API
    // 1. Tạo sản phẩm
    @Operation(summary = "Tạo sản phẩm mới")
    @PostMapping ("/{storeId}/create")
    public ResponseEntity<?> createProduct(@PathVariable Integer storeId,
                                           @RequestParam(value = "codeNumber",required = false) String codeNumber,
                                           @RequestParam("name") String name,
                                           @RequestParam("initialPrice") Integer initialPrice,
                                           @RequestParam("salePrice") Integer salePrice,
                                           @RequestParam("quantity") Integer quantity,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "note", required = false) String note,
                                           @RequestParam(value = "supplier", required = false) Integer supplierId,
                                           @RequestParam("groupProduct") Integer groupProductId,
                                           @RequestParam("userId") Integer userId,
                                           @RequestParam(value = "file", required = false) MultipartFile[] files) {
        Supplier supplier = null;
        if (supplierId != null) {
            supplier = supplierService.getSupplier(supplierId);
        }
        GroupProduct groupProduct = groupProductService.getGroupProduct(groupProductId);
        UpsertProductRequest upsertProductRequest = UpsertProductRequest.builder()
                .codeNumber(codeNumber)
                .name(name)
                .initialPrice(initialPrice)
                .salePrice(salePrice)
                .quantity(quantity)
                .description(description)
                .supplier(supplier)
                .groupProduct(groupProduct)
                .note(note)
                .store(storeId)
                .userId(userId)
                .build();

        ProductDto product = productService.createProduct(upsertProductRequest, files);

        return ResponseEntity.ok(product);
    }

    // Nhập sản phẩm qua file excel
    @Operation(summary = "Nhập sản phẩm qua file excel")
    @PostMapping("{storeId}/upload")
    public ResponseEntity<String> uploadProducts(@PathVariable Integer storeId,
                                                 @RequestParam("file") MultipartFile file) {
        try {
            List<UpsertProductRequest> productList = excelService.readExcelFile(file, storeId);
            for (UpsertProductRequest upsertProductRequest : productList) {
                productService.createProduct(upsertProductRequest, null);
            }
            return ResponseEntity.ok("Upload successful");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }

    // xuất danh sách sản phẩm qua excel
    @Operation(summary = "Xuất danh sách sản phẩm qua excel")
    @GetMapping("{storeId}/download")
    public ResponseEntity<byte[]> downloadProducts(@PathVariable Integer storeId) throws IOException {
        List<ProductPublic> productList = productRepository.findAllByStoreId(storeId);
        Workbook workbook = excelService.writeExcel(productList);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        } finally {
            bos.close();
        }
        byte[] bytes = bos.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "products.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(bytes);
    }


    // 2. Lấy thông tin về sản phẩm
    // 2.1 Thông tin tất cả sản phẩm theo cửa hàng
    @Operation(summary = "Lấy thông tin tất cả sản phẩm theo cửa hàng")
    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getAllProductFromStore(@PathVariable Integer storeId) {
        List<ProductPublic> productPublics = productService.getAllProductPublicFromStore(storeId);
        return ResponseEntity.ok(productPublics);
    }

    //2.2 Thông tin sản phẩm theo Id
    @Operation(summary = "Lấy thông tin tất cả sản phẩm theo id")
    @GetMapping("/productDetail/{id}")
    public ResponseEntity<?> getProductDetailById(@PathVariable Long id) {
        ProductPublic productPublic = productService.getProductDetailById(id);
        return ResponseEntity.ok(productPublic);
    }

    //2.3 Thông tin sản phẩm theo nhóm hàng
    @Operation(summary = "Lấy thông tin tất cả sản phẩm theo nhóm hàng")
    @GetMapping("/productDetail/{storeId}/groupProduct")
    public ResponseEntity<?> getProductDetailByGroupProduct(@PathVariable Integer storeId, @RequestParam Integer groupId) {
        List<ProductPublic> productPublics = productService.getProductDetailByGroupProduct(storeId, groupId);
        return ResponseEntity.ok(productPublics);
    }

    // 2.4 Tìm kiếm sản phẩm
    @Operation(summary = "Tìm kiếm sản phẩm theo tên")
    @GetMapping("/{storeId}/suggest")
    public ResponseEntity<?> getSuggestProduct(@RequestParam String name, @PathVariable Integer storeId) {
        List<ProductPublic> productPublics = productService.getSuggestProduct(name, storeId);
        return ResponseEntity.ok(productPublics);
    }


    // 3. Cập nhật sản phẩm
    @Operation(summary = "Cập nhật sản phẩm")
    @PatchMapping ("/update/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @RequestBody UpsertProductRequest upsertProductRequest) {
        Supplier supplier = null;
        if (upsertProductRequest.getSupplier().getId() != null) {
            supplier = supplierService.getSupplier(upsertProductRequest.getSupplier().getId());
        }
        GroupProduct groupProduct = groupProductService.getGroupProduct(upsertProductRequest.getGroupProduct().getId());
        upsertProductRequest.setGroupProduct(groupProduct);
        upsertProductRequest.setSupplier(supplier);
        ProductPublic product = productService.updateProduct(productId, upsertProductRequest);
        return ResponseEntity.ok(product);
    }



    // 4. Xoá sản phẩm
    @Operation(summary = "Xoá sản phẩm")
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Xoá sản phẩm thành công");
    }

}
