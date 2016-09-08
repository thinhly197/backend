package com.ascend.campaign.services;

import com.ascend.campaign.entities.UserPromotion;
import com.ascend.campaign.entities.UserPromotionKey;
import com.ascend.campaign.repositories.UserPromotionRepo;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPromotionServiceTest {
    @Autowired
    private UserPromotionService userPromotionService;

    @Mock
    private UserPromotionRepo userPromotionRepo;

    private UserPromotion userPromotion;
    private UserPromotionKey userPromotionKey;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userPromotionService = new UserPromotionService(userPromotionRepo);

        userPromotionKey = new UserPromotionKey("1", 1L);

        userPromotion = new UserPromotion();
        userPromotion.setUserPromotionKey(userPromotionKey);
    }

    @Test
    public void shouldReturnCreatedUserPromotionWhenCreateNonExistingUserPromotion() {
        when(userPromotionRepo.saveAndFlush(any(UserPromotion.class))).thenReturn(userPromotion);

        UserPromotion userPromotionCreated = userPromotionService.createUserPromotion(userPromotion);

        assertThat(userPromotionCreated, is(notNullValue()));

        verify(userPromotionRepo).saveAndFlush(any(UserPromotion.class));
    }

    @Test
    public void shouldReturnUserPromotionListWhenGetAllExistingUserPromotion() {
        List<UserPromotion> userPromotionList = Lists.newArrayList(userPromotion);

        when(userPromotionRepo.findAll()).thenReturn(userPromotionList);

        assertThat(userPromotionService.listUserPromotions().size(), is(not(0)));

        verify(userPromotionRepo).findAll();
    }

    @Test
    public void shouldReturnUniqueUserPromotionWhenGetExistingUserPromotionById() {
        when(userPromotionRepo.getOne(any(UserPromotionKey.class))).thenReturn(userPromotion);

        assertThat(userPromotionService.getUserPromotionById(userPromotionKey), is(notNullValue()));

        verify(userPromotionRepo).getOne(any(UserPromotionKey.class));
    }

    @Test
    public void shouldIncreaseOneExecuteTimeWhenExecuteRuleNotOverLimit() {
        userPromotion.setExeLimit(5);
        userPromotion.setExeTime(1);
        when(userPromotionRepo.getOne(any(UserPromotionKey.class))).thenReturn(userPromotion);
        when(userPromotionRepo.saveAndFlush(any(UserPromotion.class))).thenReturn(userPromotion);

        assertThat(userPromotionService.isUpdateExecuteTime(userPromotionKey), is(true));

        verify(userPromotionRepo).getOne(any(UserPromotionKey.class));
        when(userPromotionRepo.saveAndFlush(any(UserPromotion.class))).thenReturn(userPromotion);
    }

    @Test
    public void shouldNotIncreaseExecuteTimeWhenExecuteRuleOverLimit() {
        userPromotion.setExeLimit(5);
        userPromotion.setExeTime(5);
        when(userPromotionRepo.getOne(any(UserPromotionKey.class))).thenReturn(userPromotion);

        assertThat(userPromotionService.isUpdateExecuteTime(userPromotionKey), is(false));

        verify(userPromotionRepo).getOne(any(UserPromotionKey.class));
        verify(userPromotionRepo, never()).saveAndFlush(any(UserPromotion.class));
    }

    @Test
    public void shouldNotIncreaseExecuteTimeWhenExecuteRuleWithNonExistingUserPromotion() {
        when(userPromotionRepo.getOne(any(UserPromotionKey.class))).thenReturn(null);

        assertThat(userPromotionService.isUpdateExecuteTime(userPromotionKey), is(false));

        verify(userPromotionRepo).getOne(any(UserPromotionKey.class));
        verify(userPromotionRepo, never()).saveAndFlush(any(UserPromotion.class));
    }

    @Test
    public void customerCanApplyPromotionWhenExecuteRuleNotOverLimit() {
        userPromotion.setExeLimit(5);
        userPromotion.setExeTime(1);
        when(userPromotionRepo.getOne(any(UserPromotionKey.class))).thenReturn(userPromotion);

        assertThat(userPromotionService.canApplyPromotion(userPromotionKey), is(true));

        verify(userPromotionRepo).getOne(any(UserPromotionKey.class));
    }

    @Test
    public void customerCannotApplyPromotionWhenExecuteRuleOverLimit() {
        userPromotion.setExeLimit(5);
        userPromotion.setExeTime(5);
        when(userPromotionRepo.getOne(any(UserPromotionKey.class))).thenReturn(userPromotion);

        assertThat(userPromotionService.canApplyPromotion(userPromotionKey), is(false));

        verify(userPromotionRepo).getOne(any(UserPromotionKey.class));
    }

    @Test
    public void customerCannotApplyPromotionWhenExecuteRuleWithNonExistingUserPromotion() {
        when(userPromotionRepo.getOne(any(UserPromotionKey.class))).thenReturn(null);

        assertThat(userPromotionService.canApplyPromotion(userPromotionKey), is(false));

        verify(userPromotionRepo).getOne(any(UserPromotionKey.class));
        verify(userPromotionRepo, never()).saveAndFlush(any(UserPromotion.class));
    }
}
