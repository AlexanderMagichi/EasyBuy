package com.teamchallenge.easybuy.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamchallenge.easybuy.IntegrationTest;
import com.teamchallenge.easybuy.product.entity.Goods;
import com.teamchallenge.easybuy.product.entity.category.Category;
import com.teamchallenge.easybuy.product.repository.GoodsRepository;
import com.teamchallenge.easybuy.product.repository.category.CategoryRepository;
import com.teamchallenge.easybuy.review.repository.ProductReviewRepository;
import com.teamchallenge.easybuy.shop.entity.Shop;
import com.teamchallenge.easybuy.shop.repository.ShopRepository;
import com.teamchallenge.easybuy.user.entity.Authority;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.entity.UserGrantedAuthority;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
@MockitoBean(types = org.springframework.data.redis.connection.RedisConnectionFactory.class)
class ProductReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    @MockitoBean
    private com.teamchallenge.easybuy.shop.service.notification.NotificationService notificationService;

    @MockitoBean
    private com.teamchallenge.easybuy.shop.service.analytics.AnalyticsService analyticsService;

    @MockitoBean
    private com.teamchallenge.easybuy.shop.service.audit.AuditService auditService;

    @MockitoBean
    private com.teamchallenge.easybuy.payment.service.StripeService stripeService;

    private UserEntity user;
    private Goods product;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        
        user = createCustomer("customer@example.com", "John");
        
        Category category = Category.builder()
                .name("Electronics " + UUID.randomUUID())
                .build();
        category = categoryRepository.save(category);

        Shop shop = new Shop();
        shop.setShopName("Test Shop " + UUID.randomUUID());
        shop.setShopDescription("Description");
        shop.setShopStatus(Shop.ShopStatus.ACTIVE);
        shop.setSeller(user);
        shop.setCurrency("USD");
        shop.setLanguage("en");
        shop.setTimezone("UTC");
        shop = shopRepository.save(shop);

        product = Goods.builder()
                .art("ART-123")
                .name("Test Product")
                .price(new BigDecimal("100.00"))
                .mainImageUrl("http://example.com/image.jpg")
                .stock(10)
                .shop(shop)
                .category(category)
                .goodsStatus(Goods.GoodsStatus.ACTIVE)
                .discountStatus(Goods.DiscountStatus.NONE)
                .rating(0)
                .reviewsCount(0)
                .build();
        product = goodsRepository.save(product);
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void createReview_shouldUpdateGoodsRatingAndCount() throws Exception {
        Map<String, Object> reviewRequest = new HashMap<>();
        reviewRequest.put("text", "Great product!");
        reviewRequest.put("rating", 5);

        mockMvc.perform(post("/api/v1/products/{productId}/reviews", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great product!"))
                .andExpect(jsonPath("$.productRating").value(5));

        // Verify Goods updated
        Goods updatedProduct = goodsRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getRating()).isEqualTo(5);
        assertThat(updatedProduct.getReviewsCount()).isEqualTo(1);
    }

    @Test
    void getReviews_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/products/{productId}/reviews", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewsWithRatings").isEmpty());
    }

    private UserEntity createCustomer(String email, String name) {
        UserEntity customer = new UserEntity();
        customer.setEmail(email);
        customer.setPassword("password");
        customer.setFirstName(name);
        customer.setLastName("User");
        customer.addAuthority(UserGrantedAuthority.builder().authority(Authority.CUSTOMER).build());
        return userRepository.save(customer);
    }
}
