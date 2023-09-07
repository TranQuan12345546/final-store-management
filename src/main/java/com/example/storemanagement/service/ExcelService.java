package com.example.storemanagement.service;

import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.dto.request.UpsertProductRequest;
import com.example.storemanagement.enity.GroupProduct;
import com.example.storemanagement.enity.Owner;
import com.example.storemanagement.enity.Store;
import com.example.storemanagement.enity.Supplier;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.GroupProductRepository;
import com.example.storemanagement.repository.StoreRepository;
import com.example.storemanagement.repository.SupplierRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private final SupplierRepository supplierRepository;
    private final GroupProductRepository groupProductRepository;

    private final StoreRepository storeRepository;

    public ExcelService(SupplierRepository supplierRepository,
                        GroupProductRepository groupProductRepository, StoreRepository storeRepository) {
        this.supplierRepository = supplierRepository;
        this.groupProductRepository = groupProductRepository;
        this.storeRepository = storeRepository;
    }

    public List<UpsertProductRequest> readExcelFile(MultipartFile file, Integer storeId) throws IOException {
        List<UpsertProductRequest> products = new ArrayList<>();
        Store store = storeRepository.findById(storeId).orElse(null);
        Owner owner = store.getOwner();

        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            // Bỏ qua dòng tiêu đề
            if (row.getRowNum() == 0) {
                continue;
            }

            UpsertProductRequest product = new UpsertProductRequest();
            if (row.getCell(0) != null) {
                Cell cell = row.getCell(0);

                if (cell.getCellType() == CellType.NUMERIC) {
                    double numericValue = cell.getNumericCellValue();
                    long codeNumber = (long) numericValue;
                    product.setCodeNumber(String.valueOf(codeNumber));
                } else if (cell.getCellType() == CellType.STRING) {
                    String stringValue = cell.getStringCellValue().trim();
                    if (!stringValue.isEmpty()) {
                        product.setCodeNumber(stringValue);
                    }
                }
            }

            product.setName(row.getCell(1).getStringCellValue());
            product.setInitialPrice((int) row.getCell(2).getNumericCellValue());
            product.setSalePrice((int) row.getCell(3).getNumericCellValue());
            product.setQuantity((int) row.getCell(4).getNumericCellValue());
            product.setDescription(row.getCell(5).getStringCellValue());
            product.setNote(row.getCell(6).getStringCellValue());
            String supplierName = row.getCell(7).getStringCellValue();
            Supplier supplier = supplierRepository.findByName(supplierName).orElseThrow(() -> {
                throw new NotFoundException("Không tìm thấy nhà cung cấp");
            });
            product.setSupplier(supplier);
            if (row.getCell(8) != null) {
                product.setTypeProduct(row.getCell(8).getStringCellValue());
            }
            String groupProductName = row.getCell(9).getStringCellValue();
            GroupProduct groupProduct = groupProductRepository.findByName(groupProductName).orElse(null);
            if (groupProduct == null) {
                GroupProduct groupProduct1 = new GroupProduct();
                groupProduct1.setName(groupProductName);
                groupProduct1.setStore(store);
                groupProductRepository.save(groupProduct1);
                product.setGroupProduct(groupProduct1);
            }
            product.setGroupProduct(groupProduct);
            product.setStore(storeId);
            product.setUserId(owner.getId());
            products.add(product);
        }

        workbook.close();

        return products;
    }

    public Workbook writeExcel(List<ProductPublic> productList) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Product Data");
        sheet.setColumnWidth(0, 25 * 200);
        sheet.setColumnWidth(1, 25 * 256);
        sheet.setColumnWidth(2, 25 * 100);
        sheet.setColumnWidth(3, 25 * 100);
        sheet.setColumnWidth(4, 25 * 100);
        sheet.setColumnWidth(5, 25 * 300);
        sheet.setColumnWidth(6, 25 * 200);
        sheet.setColumnWidth(7, 25 * 150);
        sheet.setColumnWidth(8, 25 * 100);
        sheet.setColumnWidth(9, 25 * 150);

        // Tạo dòng tiêu đề
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã sản phẩm");
        headerRow.createCell(1).setCellValue("Tên hàng");
        headerRow.createCell(2).setCellValue("Giá vốn");
        headerRow.createCell(3).setCellValue("Giá bán");
        headerRow.createCell(4).setCellValue("Số lượng");
        headerRow.createCell(5).setCellValue("Mô tả");
        headerRow.createCell(6).setCellValue("Ghi chú");
        headerRow.createCell(7).setCellValue("Nhà cung cấp");
        headerRow.createCell(8).setCellValue("Loại hàng");
        headerRow.createCell(9).setCellValue("Nhóm hàng");

        // Ghi dữ liệu từ danh sách sản phẩm vào các dòng
        for (int i = 0; i < productList.size(); i++) {
            ProductPublic product = productList.get(i);
            Row row = sheet.createRow(i + 1);

            // Ghi giá trị vào từng ô
            row.createCell(0).setCellValue(product.getCodeNumber());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getInitialPrice());
            row.createCell(3).setCellValue(product.getSalePrice());
            row.createCell(4).setCellValue(product.getQuantity());
            row.createCell(5).setCellValue(product.getDescription());
            row.createCell(6).setCellValue(product.getNote());
            if (product.getSupplier() != null) {
                row.createCell(7).setCellValue(product.getSupplier().getName());
            }
            if (product.getTypeProduct() != null) {
                row.createCell(8).setCellValue(product.getTypeProduct().getName());
            }
            if (product.getGroupProduct() != null) {
                row.createCell(9).setCellValue(product.getGroupProduct().getName());
            }
        }



        return workbook;
    }
}
