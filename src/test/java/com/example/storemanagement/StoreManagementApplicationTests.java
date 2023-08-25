package com.example.storemanagement;

import com.example.storemanagement.constant.ProductStatus;
import com.example.storemanagement.dto.projection.ProductHistoryPublic;
import com.example.storemanagement.dto.projection.ProductPublic;
import com.example.storemanagement.enity.*;
import com.example.storemanagement.exception.NotFoundException;
import com.example.storemanagement.repository.*;
import com.example.storemanagement.service.CampaignService;
import com.example.storemanagement.service.ProductHistoryService;
import com.example.storemanagement.service.ProductService;
import com.example.storemanagement.service.SalaryService;
import com.github.javafaker.Faker;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StoreManagementApplicationTests {

}
