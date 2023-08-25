package com.example.storemanagement.service;

import com.example.storemanagement.dto.ProductDto;
import com.example.storemanagement.dto.projection.OrderPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.request.ReturnRequest;
import com.example.storemanagement.dto.request.UpsertOrder;
import com.example.storemanagement.enity.*;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.storemanagement.service.ProductService.getCodeNumber;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductHistoryService productHistoryService;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private UsedVoucherRepository usedVoucherRepository;
    @Autowired
    private UserRepository userRepository;

    public List<OrderPublic> createOrder(UpsertOrder upsertOrder, Integer storeId) {
        List<OrderPublic> orderPublics = new ArrayList<>();
        List<UpsertOrder.ProductItem> productItems = upsertOrder.getProducts();
        for (UpsertOrder.ProductItem productItem : productItems) {
            Order order = new Order();
            UsedVoucher usedVoucher = new UsedVoucher();
            Store store = storeRepository.findById(storeId).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy store có id" + storeId);
            });
            order.setStore(store);

            String generatedOrderNumber = generateUniqueOrderNumberForStore(storeId);
            order.setOrderNumber(generatedOrderNumber);

            Long productId = productItem.getProductId();
            Product product = productRepository.findById(productId).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy product có id" + productId);
            });;
            order.setProduct(product);
            order.setProductName(product.getName());
            order.setGroupProductName(product.getGroupProduct().getName());
            usedVoucher.setProduct(product);

            Integer quantity = productItem.getQuantity();
            order.setQuantity(quantity);
            // chỉnh sửa số lượng tồn kho
            product.setQuantity(product.getQuantity() - quantity);

            Integer userId = productItem.getUserId();
            User user = userRepository.findById(userId).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy user có id" + userId);
            });
            order.setUser(user);
            productHistoryService.saleProduct(product, user.getFullName(), quantity);

            Integer clientId = productItem.getClientId();
            if (clientId != null) {
                Client client = clientRepository.findById(clientId).orElseThrow(() -> {
                    throw new NotFoundException("Không tìm thấy client có id" + clientId);
                });
                order.setClient(client);
                usedVoucher.setClient(client);
            }

            order.setDiscount(productItem.getDiscount());
            order.setTotalPrice(productItem.getTotalPrice());
            orderRepository.save(order);
            String voucherCode = productItem.getVoucherCode();
            if (voucherCode != null && !voucherCode.isEmpty()) {
                Voucher voucher = voucherRepository.findByCode(voucherCode);
                if (voucher != null) {
                    voucher.setUsedQuantity(voucher.getUsedQuantity() + 1);
                    voucherRepository.save(voucher);
                    usedVoucher.setVoucher(voucher);
                    usedVoucher.setOrder(order);
                }
            }
            usedVoucherRepository.save(usedVoucher);
            orderPublics.add(OrderPublic.of(order));
        }

        return orderPublics;
    }

    private String getCodeNumber(Long quantity) {
        int digits = 6;
        int limit = 999999;

        while (quantity > limit) {
            digits++;
            limit = limit * 10 + 9;
        }
        String format = "";
        if (digits < 10) {
            format = "OD%0" + digits + "d";
        } else {
            format = "OD%" + digits + "d";
        }
        String generatedCodeNumber = String.format(format, quantity + 1);

        return generatedCodeNumber;
    }

    private String generateUniqueOrderNumberForStore(Integer storeId) {
        Long orderCount = orderRepository.countByStoreId(storeId);
        String codeNumber = getCodeNumber(orderCount);

        while (orderRepository.findByOrderNumberAndStoreId(codeNumber, storeId) != null) {
            codeNumber = getCodeNumber(orderCount+1);
        }
        return codeNumber;
    }

    public Page<OrderPublic> getAllOrderRelatedStore(Integer storeId, Integer page, Integer pageSize) {
        Page<Order> orders = orderRepository.findAllByStoreIdAndIsReturnFalse(storeId, PageRequest.of(page - 1, pageSize, Sort.by("orderDate").descending()));

        List<OrderPublic> orderPublics = orders.getContent()
                .stream()
                .map(OrderPublic::of)
                .collect(Collectors.toList());
        return new PageImpl<>(orderPublics, orders.getPageable(), orders.getTotalElements());
    }

    public Page<OrderPublic> getAllReturnOrderRelatedStore(Integer storeId, Integer page, Integer pageSize) {
        Page<Order> orders = orderRepository.findAllByStoreIdAndIsReturnTrue(storeId, PageRequest.of(page - 1, pageSize, Sort.by("returnCreatedAt").descending()));

        List<OrderPublic> orderPublics = orders.getContent()
                .stream()
                .map(OrderPublic::of)
                .collect(Collectors.toList());
        return new PageImpl<>(orderPublics, orders.getPageable(), orders.getTotalElements());
    }



    public List<OrderPublic> getAllOrderRelatedStore(Integer storeId) {
        List<Order> orders = orderRepository.findAllByStoreId(storeId);
        return orders.stream()
                .map(OrderPublic::of)
                .collect(Collectors.toList());
    }

    public List<OrderPublic> getAllOrderRelatedProduct(Long productId) {
        List<Order> orders = orderRepository.findAllByProductId(productId);
        return orders.stream().map(order -> OrderPublic.of(order)).collect(Collectors.toList());
    }

    public List<OrderPublic> findById(List<Long> orderId) {
        List<Order> orders = orderRepository.findAllById(orderId);
        return orders.stream().map(order -> OrderPublic.of(order)).collect(Collectors.toList());
    }

    public List<OrderPublic> returnOrder(ReturnRequest request) {

        List<Order> orders = orderRepository.findAllById(request.getOrderIds());

        for (Order order : orders) {
            LocalDateTime returnAcceptedDay = order.getOrderDate().plusDays(10);
            LocalDateTime returnDay = LocalDateTime.now();
            if (!returnDay.isAfter(returnAcceptedDay)) {
                order.setIsReturn(true);
                order.setReturnCreatedAt(LocalDateTime.now());
                Product product = productRepository.findById(order.getProduct().getId()).orElseThrow(() -> {
                    throw new NotFoundException("Không tìm thấy sản phẩm với order này");
                });
                product.setQuantity(product.getQuantity() + order.getQuantity());
                if (request.getUserId() != null) {
                    User user = userRepository.findById(request.getUserId()).orElseThrow(() -> {
                        throw new NotFoundException("Không tìm thấy người dùng " + request.getUserId());
                    });
                    productHistoryService.returnProduct(product, user.getFullName(), order.getQuantity());
                    order.setReturnCreatedBy(user.getFullName());
                }

                orderRepository.save(order);
            } else {
                throw new IllegalStateException("Không thể trả hàng sau 10 ngày từ ngày tạo đơn hàng");
            }
        }

        return orders.stream().map(order -> OrderPublic.of(order)).collect(Collectors.toList());
    }

    public List<ProductPublic> getSuggestProduct(String name, Integer storeId) {
        List<ProductPublic> productPublicList = new ArrayList<>();
        List<ProductPublic> products = productRepository.findAllByStoreId(storeId);
        List<Order> orders = orderRepository.findAllByStoreId(storeId);
        for (ProductPublic product : products) {
            String lowercaseProductName = product.getName().toLowerCase();
            String lowercaseProductCode = product.getCodeNumber().toLowerCase();
            if (lowercaseProductName.contains(name) || lowercaseProductCode.contains(name)) {
                productPublicList.add(product);
            }
        }
        for (Order order : orders) {
            String lowercaseOrderNumber = order.getOrderNumber().toLowerCase();
            if (lowercaseOrderNumber.contains(name)) {
                Product product = productRepository.findById(order.getProduct().getId()).orElseThrow(() -> {
                    throw new NotFoundException("Không tìm thấy sản phẩm");
                });
                ProductPublic productPublic = ProductPublic.of(product);
                boolean exists = false;
                for (ProductPublic existingProduct : productPublicList) {
                    if (existingProduct.getId().equals(productPublic.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    productPublicList.add(productPublic);
                }

            }
        }
        return productPublicList;
    }
}
