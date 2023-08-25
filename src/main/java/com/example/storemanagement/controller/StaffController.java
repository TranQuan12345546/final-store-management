package com.example.storemanagement.controller;

import com.example.storemanagement.dto.StaffSalaryDto;
import com.example.storemanagement.dto.projection.ShiftAssignmentPublic;
import com.example.storemanagement.dto.projection.StaffPublic;
import com.example.storemanagement.dto.projection.WorkShiftPublic;
import com.example.storemanagement.dto.request.UpsertStaffRequest;
import com.example.storemanagement.enity.SalaryPerHour;
import com.example.storemanagement.enity.TokenConfirm;
import com.example.storemanagement.enity.User;
import com.example.storemanagement.exception.BadRequestException;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.TokenConfirmRepository;
import com.example.storemanagement.repository.UserRepository;
import com.example.storemanagement.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping("/staff")
public class StaffController {
    @Autowired
    private StaffService staffService;

    @Autowired
    private WorkShiftService workShiftService;

    @Autowired
    private ShiftAssignmentService shiftAssignmentService;
    @Autowired
    private WebService webService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenConfirmRepository tokenConfirmRepository;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private MailService mailService;

    @Autowired
    private SalaryService salaryService;

    // Trang thông tin nhân viên
    @GetMapping("/{storeId}")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getStaffInfo(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<StaffPublic> staffPublicList = staffService.getAllStaffFromStore(storeId);
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("staffList", staffPublicList);
            return "web/staff/staff-info";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }

    }

    // Trang lịch làm việc
    @GetMapping("/{storeId}/timetable")
    @PreAuthorize("hasRole('ROLE_OWNER')")
    public String getTimeSheets(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            List<StaffPublic> staffPublicList = staffService.getAllStaffFromStore(storeId);
            LocalDate today = LocalDate.now();
            List<WorkShiftPublic> workShifts = shiftAssignmentService.getWorkShiftByDateSelected(storeId, today);
            List<ShiftAssignmentPublic> shiftAssignmentPublics = shiftAssignmentService.getAllShiftAssignmentInWeek(storeId, LocalDate.now());
            List<LocalDate> dayOfWeek = shiftAssignmentService.getDayOfWeek(LocalDate.now());
            Object user = webService.getUserInfo(authentication.getName());
            model.addAttribute("userInfo", user);
            model.addAttribute("staffList", staffPublicList);
            model.addAttribute("workShiftList", workShifts);
            model.addAttribute("shiftAssignmentList", shiftAssignmentPublics);
            model.addAttribute("dayOfWeek", dayOfWeek);
            return "web/staff/time-sheets";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }

    }


    // Trang bảng chấm công
    @GetMapping("/{storeId}/timekeeping")
    @PreAuthorize("hasAnyRole('ROLE_OWNER', 'ROLE_STAFF')")
    public String getTimekeeping(@PathVariable Integer storeId, Model model, Authentication authentication) {
        boolean hasAccessToStore = webService.checkUserHasAccessToStore(authentication.getName(), storeId);
        if (hasAccessToStore) {
            LocalDate today = LocalDate.now();
            List<WorkShiftPublic> workShifts = shiftAssignmentService.getWorkShiftByDateSelected(storeId, today);
            List<LocalDate> dayOfWeek = shiftAssignmentService.getDayOfWeek(today);
            List<ShiftAssignmentPublic> shiftAssignmentPublics = shiftAssignmentService.getAllShiftAssignmentInWeek(storeId, LocalDate.now());
            Object user = webService.getUserInfo(authentication.getName());
            User user1 = (User) user;
            String ownerName = ownerService.getOwnerStoreName(storeId);
            model.addAttribute("ownerName", ownerName);
            model.addAttribute("userInfo", user1);
            model.addAttribute("userName", user1.getFullName());
            model.addAttribute("workShiftList", workShifts);
            model.addAttribute("shiftAssignmentList", shiftAssignmentPublics);
            model.addAttribute("dayOfWeek", dayOfWeek);
            return "web/staff/timekeeping";
        } else {
            throw new BadRequestException("Bạn không có quyền truy cập nội dung này");
        }

    }

    // Trang tính lương
    @GetMapping("/{storeId}/salary")
    @PreAuthorize("hasAnyRole('ROLE_OWNER')")
    public String getSalaryPage(@PathVariable Integer storeId, Authentication authentication, Model model) {
        Object user = webService.getUserInfo(authentication.getName());
        String ownerName = ownerService.getOwnerStoreName(storeId);
        Integer salaryPerHour = salaryService.getSalaryPerHour(storeId);
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate now = LocalDate.now();
        List<StaffSalaryDto> staffSalaryDtos = salaryService.getAllSalaryByAllStaffByDate(storeId, startOfMonth, now);
        model.addAttribute("staffSalaryList", staffSalaryDtos);
        model.addAttribute("salaryPerHour", salaryPerHour);
        model.addAttribute("ownerName", ownerName);
        model.addAttribute("userInfo", user);
        return "web/staff/salary";
    }

    // Danh sách API
    // Tạo nhân viên mới
    @PostMapping("/{storeId}/create")
    public ResponseEntity<?> createNewStaff(@PathVariable Integer storeId,
                                            @RequestParam("fullName") String fullName,
                                            @RequestParam("username") String username,
                                            @RequestParam("password") String password,
                                            @RequestParam("email") String email,
                                            @RequestParam(value = "address", required = false) String address,
                                            @RequestParam(value = "phone", required = false) String phone,
                                            @RequestParam("birthday") String birthday,
                                            @RequestParam(value = "file", required = false) MultipartFile file) {
        UpsertStaffRequest upsertStaffRequest = UpsertStaffRequest.builder()
                .fullName(fullName)
                .username(username)
                .password(password)
                .email(email)
                .address(address)
                .phone(phone)
                .birthday(birthday)
                .build();
        StaffPublic staffPublic = staffService.createNewStaff(storeId, upsertStaffRequest, file);
        return ResponseEntity.ok(staffPublic);
    }

    // Upload avatar nhân viên
    @Operation(summary = "Thay đổi avatar nhân viên")
    @PostMapping("/avatar/{staffId}")
    public ResponseEntity<?> uploadStaffAvatar(@PathVariable Integer staffId,
                                               @RequestParam("file") MultipartFile file) {
        staffService.uploadStaffAvatar(file, staffId);
        return ResponseEntity.ok("upload thành công");
    }

    // Update thông tin nhân viên
    @Operation(summary = "Cập nhật thông tin nhân viên")
    @PutMapping("/update")
    public ResponseEntity<?> updateStaffInfo(@RequestBody UpsertStaffRequest upsertStaffRequest) {
        StaffPublic staffPublic = staffService.updateStaffInfo(upsertStaffRequest);
        return ResponseEntity.ok(staffPublic);
    }
    @Operation(summary = "Xoá nhân viên")
    @DeleteMapping("/delete/{staffId}")
    public ResponseEntity<?> deleteStaff(@PathVariable Integer staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.ok("Delete successful.");
    }

    // Check-in ca làm việc
    @Operation(summary = "Gửi yêu cầu check in ca làm việc")
    @PostMapping("/check-in/{userId}")
    public ResponseEntity<?> checkInShift(@PathVariable Integer userId) throws MessagingException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new RuntimeException("Not found user");
                });

        TokenConfirm tokenConfirm = TokenConfirm.builder()
                .token(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();

        tokenConfirmRepository.save(tokenConfirm);
        mailService.sendCheckInToken(
                user.getEmail(),
                user.getFullName(),
                tokenConfirm.getToken()
        );
        return ResponseEntity.ok("Gửi email thành công");
    }

    @Operation(summary = "Kiểm tra tính hợp lệ token xác nhận")
    @GetMapping("/check-in-confirm/{token}")
    public ResponseEntity<?> checkToken(@PathVariable String token) {
        // Kiểm tra token có hợp lệ hay không
        Optional<TokenConfirm> optionalTokenConfirm = tokenConfirmRepository.findByToken(token);
        if(optionalTokenConfirm.isEmpty()) {
            throw new NotFoundException("Mã kích hoạt không hợp lệ");
        }
        // Kiểm tra token đã được kích hoạt hay chưa
        TokenConfirm tokenConfirm = optionalTokenConfirm.get();
        if(tokenConfirm.getConfirmedAt() != null) {
            throw new NotFoundException("Mã kích hoạt đã được sử dụng");
        }

        // Kiểm tra token đã hết hạn hay chưa
        if(tokenConfirm.getExpiredAt().isBefore(LocalDateTime.now())) {
           throw new NotFoundException("Mã kích hoạt đã hết hạn");
        }

        tokenConfirm.setConfirmedAt(LocalDateTime.now());
        tokenConfirmRepository.save(tokenConfirm);
        return ResponseEntity.ok("Xác nhận thành công");
    }
}
















