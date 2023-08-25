package com.example.storemanagement.controller;

import com.example.storemanagement.dto.projection.GroupProductPublic;
import com.example.storemanagement.dto.projection.OrderPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.request.ReturnRequest;
import com.example.storemanagement.dto.request.UpsertOrder;
import com.example.storemanagement.enity.Owner;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.UserRepository;
import com.example.storemanagement.service.GroupProductService;
import com.example.storemanagement.service.OrderService;
import com.example.storemanagement.service.WebService;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private GroupProductService groupProductService;

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private WebService webService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StoreRepository storeRepository;


    // Trang order-history
    @GetMapping("/{storeId}/order-history")
    @PreAuthorize("hasAnyRole('ROLE_OWNER', 'ROLE_STAFF')")
    public String getOrderHistoryPage(@RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "18") Integer pageSize,
                                      @PathVariable Integer storeId, Model model, Authentication authentication) {
       boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            Page<OrderPublic> orders = orderService.getAllOrderRelatedStore(storeId, page, pageSize);
            List<GroupProductPublic> groupProductList = groupProductService.getAllGroupProductByStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("page", orders);
            model.addAttribute("currentPage", page);
            model.addAttribute("storeId", storeId);
            model.addAttribute("groupProductList", groupProductList);
            return "web/products/order-history";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    // Trang return
    @GetMapping("/{storeId}/return")
    @PreAuthorize("hasAnyRole('ROLE_OWNER', 'ROLE_STAFF')")
    public String getReturnPage(@RequestParam(required = false, defaultValue = "1") Integer page,
                                @RequestParam(required = false, defaultValue = "18") Integer pageSize,
                                @PathVariable Integer storeId, Model model, Authentication authentication) {
       boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            Page<OrderPublic> orders = orderService.getAllReturnOrderRelatedStore(storeId, page, pageSize);
            List<GroupProductPublic> groupProductList = groupProductService.getAllGroupProductByStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("page", orders);
            model.addAttribute("currentPage", page);
            model.addAttribute("storeId", storeId);
            model.addAttribute("groupProductList", groupProductList);
            return "web/products/return";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getAllOrderRelatedStore(@PathVariable Integer storeId) {
        List<OrderPublic> orders = orderService.getAllOrderRelatedStore(storeId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getAllOrderRelatedProduct(@PathVariable Long productId) {
        List<OrderPublic> orders = orderService.getAllOrderRelatedProduct(productId);
        return ResponseEntity.ok(orders);
    }

    // Tạo đơn hàng
    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createOrder(@Valid @RequestBody UpsertOrder upsertOrder, @PathVariable Integer storeId) {
        List<OrderPublic> orders = orderService.createOrder(upsertOrder, storeId);
        return ResponseEntity.ok(orders);
    }

    // In hoá đơn

    @GetMapping("order-page/{productId}")
    public String getOrderPage(@PathVariable Integer productId, Model model) {
        List<OrderPublic> orders = orderService.getAllOrderRelatedStore(productId);
        model.addAttribute("orderList", orders);
        return "order-invoice";
    }


    @PostMapping("/{storeId}/convert")
    public ResponseEntity<ByteArrayResource> convertToPdf(@RequestParam List<Long> orderIds,@PathVariable Integer storeId) throws IOException {
        Store store = storeRepository.findById(storeId).orElse(null);
        List<OrderPublic> orders = orderService.findById(orderIds);
        Long orderPrice = 0L;
        Integer orderDiscount = 0;
        Long orderTotal = 0L;
        for (OrderPublic orderPublic : orders) {
            orderPrice += orderPublic.getTotalPrice() + orderPublic.getDiscount();
            orderDiscount += orderPublic.getDiscount();
            orderTotal += orderPublic.getTotalPrice();
        }
        Context context = new Context();
        // Đặt các biến và dữ liệu cần thiết vào context
        context.setVariable("tenCuaHang", store.getName());
        context.setVariable("diaChi", store.getAddress());
        context.setVariable("soDienThoai", store.getPhone());
        context.setVariable("orderList", orders);
        context.setVariable("orderPrice", orderPrice);
        context.setVariable("orderDiscount", orderDiscount);
        context.setVariable("orderTotal", orderTotal);


        // Render file HTML với Thymeleaf để có phiên bản đã thay đổi
        String htmlContent = templateEngine.process("order-invoice.html", context);



        // Tạo một ByteArrayOutputStream để lưu trữ file PDF đầu ra
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Tạo đối tượng PdfWriter để ghi dữ liệu vào file PDF
        PdfWriter writer = new PdfWriter(outputStream);

        // Tạo đối tượng PdfDocument với PdfWriter
        PdfDocument pdfDoc = new PdfDocument(writer);

        // Tạo đối tượng ConverterProperties để cấu hình quá trình chuyển đổi
        ConverterProperties converterProperties = new ConverterProperties();


        // Chuyển đổi phiên bản đã thay đổi của HTML thành PDF sử dụng ConverterProperties
        HtmlConverter.convertToPdf(htmlContent, pdfDoc, converterProperties);

        // Đóng tài liệu PDF
        pdfDoc.close();

        // Lấy dữ liệu từ ByteArrayOutputStream
        byte[] pdfBytes = outputStream.toByteArray();

        // Tạo một đối tượng ByteArrayResource từ dữ liệu PDF
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        // Cấu hình các header cho phản hồi
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf");

        // Trả về file PDF cho client
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    // Trả hàng
    @PutMapping("/return/{storeId}")
    public ResponseEntity<?> returnOrder (@PathVariable String storeId, @RequestBody ReturnRequest request) {
        List<OrderPublic> orderPublics = orderService.returnOrder(request);
        return ResponseEntity.ok("Trả hàng thành công");
    }

    // 2.3 Gợi ý tìm kiếm đơn hàng
    @GetMapping("/{storeId}/suggest")
    public ResponseEntity<?> getSuggestProduct(@RequestParam String name, @PathVariable Integer storeId) {
        List<ProductPublic> productPublics = orderService.getSuggestProduct(name, storeId);
        return ResponseEntity.ok(productPublics);
    }
}
