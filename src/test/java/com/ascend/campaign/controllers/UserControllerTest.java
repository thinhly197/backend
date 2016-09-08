package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.Promotion;
import com.ascend.campaign.entities.User;
import com.ascend.campaign.entities.UserPromotion;
import com.ascend.campaign.entities.UserPromotionKey;
import com.ascend.campaign.services.UserPromotionService;
import com.ascend.campaign.services.UserService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {
    @InjectMocks
    private UserController controller;

    private MockMvc mvc;

    @Mock
    private UserService userService;

    @Mock
    private UserPromotionService userPromotionService;

    private User user;
    private Promotion promotion;
    private UserPromotion userPromotion;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();

        user = new User();
        user.setId(1L);
        user.setEmail("setFlatDiscountData@mail.com");
        user.setCustomerId("CUSTOMER_1");

        promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Test");

        UserPromotionKey userPromotionKey = new UserPromotionKey("1", 1L);

        userPromotion = new UserPromotion();
        userPromotion.setUserPromotionKey(userPromotionKey);
    }

    @Test
    public void shouldCreatedWhenCreateNonExistingUser() throws Exception {
        when(userService.createUser(any(User.class))).thenReturn(user);

        mvc.perform(post("/api/v1/users")
                .content("{\"email\":\"setFlatDiscountData@mail.com\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email", is("setFlatDiscountData@mail.com")))
                .andExpect(status().isCreated());

        verify(userService).createUser(any(User.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnUserListWhenGetAllExistingUser() throws Exception {
        List<User> users = Lists.newArrayList(user);

        when(userService.listUsers()).thenReturn(users);

        mvc.perform(get("/api/v1/users/"))
                .andExpect(jsonPath("$.data.[*].email", hasItems(endsWith("setFlatDiscountData@mail.com"))))
                .andExpect(status().isOk());

        verify(userService).listUsers();
    }

    @Test
    public void shouldReturnUniqueUserWhenGetExistingUserByCustomerId() throws Exception {
        when(userService.getUserByCustomerId(anyString())).thenReturn(user);

        mvc.perform(get("/api/v1/users/customers/CUSTOMER_1"))
                .andExpect(jsonPath("$.data.email", is("setFlatDiscountData@mail.com")))
                .andExpect(status().isOk());

        verify(userService).getUserByCustomerId(anyString());
    }

    @Test
    public void shouldReturnUserUpdatedWhenUpdateCustomerIdInExistingUser() throws Exception {
        when(userService.updateCustomerId(anyLong(), anyString())).thenReturn(user);

        mvc.perform(put("/api/v1/users/1/CUSTOMER_1"))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.customer_id", is("CUSTOMER_1")))
                .andExpect(status().isOk());

        verify(userService).updateCustomerId(anyLong(), anyString());
    }

    @Test
    public void shouldCreatedWhenCreateNonExistingUserPromotion() throws Exception {
        when(userPromotionService.createUserPromotion(any(UserPromotion.class))).thenReturn(userPromotion);

        mvc.perform(post("/api/v1/users/promotions")
                .content("{\"customer_id\":1,\"promotion_id\":1,\"exe_limit\":\"5\", \"exe_time\":\"0\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.user_promotion_id.customer_id", is("1")))
                .andExpect(jsonPath("$.data.user_promotion_id.promotion_id", is(1)))
                .andExpect(status().isCreated());

        verify(userPromotionService).createUserPromotion(any(UserPromotion.class));
    }

    @Test
    public void shouldReturnUserPromotionListWhenGetAllExistingUserPromotion() throws Exception {
        List<UserPromotion> userPromotionList = Lists.newArrayList(userPromotion);

        when(userPromotionService.listUserPromotions()).thenReturn(userPromotionList);

        mvc.perform(get("/api/v1/users/promotions"))
                .andExpect(jsonPath("$.data.[0].user_promotion_id.customer_id", is("1")))
                .andExpect(jsonPath("$.data.[0].user_promotion_id.promotion_id", is(1)))
                .andExpect(status().isOk());

        verify(userPromotionService).listUserPromotions();
    }

    @Test
    public void shouldReturnUserPromotionWhenGetExistingUserPromotionById() throws Exception {
        when(userPromotionService.getUserPromotionById(any(UserPromotionKey.class))).thenReturn(userPromotion);

        mvc.perform(get("/api/v1/users/1/promotions/1"))
                .andExpect(jsonPath("$.data.user_promotion_id.customer_id", is("1")))
                .andExpect(jsonPath("$.data.user_promotion_id.promotion_id", is(1)))
                .andExpect(status().isOk());

        verify(userPromotionService).getUserPromotionById(any(UserPromotionKey.class));
    }
}