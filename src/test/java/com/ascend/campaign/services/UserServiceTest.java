package com.ascend.campaign.services;

import com.ascend.campaign.entities.User;
import com.ascend.campaign.repositories.UserRepo;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Mock
    private UserRepo userRepo;

    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserService(userRepo);

        user = new User();
        user.setId(1L);
        user.setEmail("setFlatDiscountData@mail.com");
        user.setCustomerId("CUSTOMER_1");
    }

    @Test
    public void shouldReturnUserUpdatedWhenUpdateCustomerIdInExistingUser() {
        when(userRepo.getOne(anyLong())).thenReturn(user);
        when(userRepo.saveAndFlush(any(User.class))).thenReturn(user);

        User userUpdated = userService.updateCustomerId(1L, "CUSTOMER_1");

        assertThat(userUpdated.getId(), is(1L));
        assertThat(userUpdated.getCustomerId(), is("CUSTOMER_1"));

        verify(userRepo).getOne(anyLong());
        verify(userRepo).saveAndFlush(any(User.class));
    }

    @Test
    public void shouldCreatedWhenCreateNonExistingUserInDb() {
        when(userRepo.saveAndFlush(any(User.class))).thenReturn(user);

        User userCreated = userService.createUser(user);

        assertThat(userCreated, is(notNullValue()));

        verify(userRepo).saveAndFlush(any(User.class));
    }

    @Test
    public void shouldListUsersWhenGetAllExistingUserInDb() {
        List<User> users = Lists.newArrayList(user);

        when(userRepo.findAll()).thenReturn(users);

        assertThat(userService.listUsers().size(), is(not(0)));

        verify(userRepo).findAll();
    }

    @Test
    public void shouldReturnUniqueUserWhenGetExistingUserByCustomerId() {
        when(userRepo.findByCustomerId(anyString())).thenReturn(user);

        assertThat(userService.getUserByCustomerId("CUSTOMER_1"), is(notNullValue()));

        verify(userRepo).findByCustomerId(anyString());
    }
}
