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
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private StaffRepository staffRepository;
	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private GroupProductRepository groupProductRepository;

	@Autowired
	private SupplierRepository supplierRepository;
	@Autowired
	private ProductHistoryRepository productHistoryRepository;

	@Autowired
	private ProductHistoryService productHistoryService;
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CampaignService campaignService;
	@Autowired
	private CampaignRepository campaignRepository;
	@Autowired
	private ShiftAssignmentRepository shiftAssignmentRepository;
	@Autowired
	private WorkShiftRepository workShiftRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SalaryService salaryService;

	@Test
	void contextLoads() {
	}

	@Test
	void save_roles() {
		List<Role> roles = List.of(
				new Role(1, "ADMIN"),
				new Role(2, "OWNER"),
				new Role(3, "STAFF")
		);

		roleRepository.saveAll(roles);
	}

	@Test
	void save_users() {
		Role ownerRole = roleRepository.findByName("OWNER").orElse(null);
		Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
		Role staffRole = roleRepository.findByName("STAFF").orElse(null);


//		Admin admin = new Admin(null, "Quant23ran", "Quanco123", "111", "hi2eưen@gmail.com", null, null, null, adminRole);
//		Owner owner1 = new Owner(null, "Quanan1qưeqe22323", "Quaqeqe232323213", "1121", "h12213êien@gmail.com", null, null, null, null, ownerRole, null , null, null);
//		List<Owner> owners = new ArrayList<>();
//		owners.add(owner1);
		Store store = storeRepository.findById(1).orElseThrow(() -> {
			throw new NotFoundException("");
		});
//		Staff staff1 = new Staff(null, "eqqweqe", "Quanqqưeqwưe123213", "1qe11", "h11123qưeưeqqwe23ien@gmail.com", "h11123qưeưeqqwe23ien@gmail.com", "012313123", false,null, staffRole, null , store , null);
		List<Staff> staffs = new ArrayList<>();
//		staffs.add(staff1);
//		adminRepository.save(admin);
//		ownerRepository.saveAll(owners);
		staffRepository.saveAll(staffs);
	}

	@Test
	void save_store() {
		List<Owner> owners = ownerRepository.findAll();
		for (Owner i : owners) {
			Store store = new Store();
			store.setName(i.getUsername());
			store.setOwner(i);
			storeRepository.save(store);
		}
	}

	@Test
	void save_group_product() {
		GroupProduct groupProduct = new GroupProduct();
		groupProduct.setName("siêu nhân");
		groupProduct.setStore(storeRepository.findAll().stream().findFirst().get());
		groupProductRepository.save(groupProduct);
	}

	@Test
	void save_supplier() {
		Supplier supplier = new Supplier();
		supplier.setName("Doanh nghiệp 1");
		supplierRepository.save(supplier);
	}

	@Test
	void save_product() {

	}

	@Test
	void getProduct(){
		Product product = productRepository.findByCodeNumberAndStoreId("SP00001", 1);
		assertThat(product).isEqualTo(null);
	}

	@Test
	void getAllProductPublicFromStore() {
		List<ProductPublic> productPublics = productRepository.findAllByStoreId(1);
		assertThat(productPublics.size()).isEqualTo(30);
	}

	@Test
	@Transactional
	void getProductById() {
		Optional<Product> product = productRepository.findById(10L);
		assertThat(product).isEqualTo(null);
	}

	@Test
	void addProduct() {
		Faker faker = new Faker();
		Owner owner = ownerRepository.findById(1).orElse(null);
		List<Store> storeList = storeRepository.findAll();
		List<GroupProduct> groupProductList = groupProductRepository.findAll();
		List<Supplier> supplierList = supplierRepository.findAll();
		Random rd = new Random();
		for (int i = 0; i < 25; i++) {
			Store store = storeList.get(0);
			GroupProduct groupProduct = groupProductList.get(rd.nextInt(groupProductList.size()));
			Supplier supplier = supplierList.get(rd.nextInt(supplierList.size()));


			// Tao product
			Product product = Product.builder()
					.codeNumber(String.valueOf(rd.nextLong(10000000000L)))
					.name(faker.commerce().productName())
					.initialPrice(rd.nextInt(1000000))
					.salePrice(rd.nextInt(1000000))
					.quantity(rd.nextInt(100))
					.note(faker.commerce().material())
					.description("description " + (i + 1))
					.groupProduct(groupProduct)
					.supplier(supplier)
					.store(store)
					.createdBy(owner.getUsername())
					.status(ProductStatus.INSTOCK)
					.build();


			productRepository.save(product);
			productHistoryService.createProduct(product);
		}
	}
	@Test
	@Transactional
	void getProductHistoryByProductId() {
		List<ProductHistory> productHistory = productHistoryRepository.findAllByProductId(2L, Sort.by("createdBy").descending());
		assertThat(productHistory).isEqualTo(null);
	}

	@Test
	void getInventory() {
		Long inventory = productRepository.getTotalValueByStoreId(1);
		assertThat(inventory).isEqualTo(1000000);
	}

	@Test
	void getTotalProduct() {
		Integer inventory = productRepository.countAvailableProductsByStoreId(1);
		assertThat(inventory).isEqualTo(1000000);
	}

	@Test
	void getAllOrder() {
		List<Order> inventory = orderRepository.findAllByProductId(5L);
		assertThat(inventory).isEqualTo(2);
	}

	@Test
	void getTotalRevenueDay() {
//		LocalDateTime today = LocalDateTime.now();
//		LocalDateTime yesterday = today.minusDays(1);
//		LocalDateTime startOfYesterday = yesterday.with(LocalTime.MIN);
//		LocalDateTime endOfYesterday = yesterday.with(LocalTime.MAX);
//		Long totalPriceSumYesterdayOptional =  orderRepository.getTotalPriceSumForDay(store, startOfYesterday, endOfYesterday);
//		assertThat(totalPriceSumYesterdayOptional).isEqualTo(null);
	}

	@Test
	void getDayOfWeek() {
		LocalDate currentDate = LocalDate.now();

		List<LocalDate> daysOfWeek = new ArrayList<>();
		DayOfWeek firstDayOfWeek = DayOfWeek.MONDAY;


		for (int i = 0; i < DayOfWeek.values().length; i++) {
			DayOfWeek dayOfWeek = firstDayOfWeek.plus(i);
			LocalDate day = currentDate.with(dayOfWeek);

			daysOfWeek.add(day);
		}


		for (LocalDate day : daysOfWeek) {
			System.out.println(day);
		}
	}

	@Test
	void createShiftAssignment() {

	}

	@Test
	void findAllByWorkDayAndStoreId() {
		LocalDate currentDate = LocalDate.of(2023, 8, 15);
		List<ShiftAssignment> shiftAssignments = shiftAssignmentRepository.findAllByWorkDayAndStoreId(currentDate, 1);
	}

	@Test
	void findByWorkDayAndWorkShift_Id() {
		LocalDate currentDate = LocalDate.of(2023, 8, 16);
		ShiftAssignment shiftAssignment = shiftAssignmentRepository.findByWorkDayAndWorkShiftId(currentDate, 14);
	}

	@Test
	void saveAdmin() {
		Role role = roleRepository.findByName("ADMIN").orElse(null);
		User adminUser = userRepository.findByUsername("admin").orElse(null);
		if (adminUser == null) {
			adminUser = new Admin();
			adminUser.setUsername("admin");
			adminUser.setPassword(passwordEncoder.encode("111"));
			adminUser.setRole(role);
			userRepository.save(adminUser);
		}
	}

	@Test
	void findUser() {
		User user = userRepository.findByUsername("xomchua1234").orElseThrow(() -> {
			throw new NotFoundException("Không tìm thấy");
		});
		System.out.println(user.getPassword());
	}

	@Test
	void getStore() {
		Staff staff = staffRepository.findById(4).orElseThrow(() -> {
			throw new NotFoundException("Không tìm thấy nhân viên");
		});
		Store store = staff.getStore();
		System.out.println(store.getId());
	}

//	@Test
//	void getSalaryStaffByDate() {
//		LocalDate startDate = LocalDate.of(2023, 8,1);
//		LocalDate endDate = LocalDate.of(2023, 8,30);
//		Double totalWorkHour = salaryService.getSalaryStaffByDate(1,5, 50000,startDate, endDate);
//		System.out.println(totalWorkHour);
//	}
}
