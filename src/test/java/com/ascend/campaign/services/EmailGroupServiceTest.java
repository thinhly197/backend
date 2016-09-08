package com.ascend.campaign.services;

import com.ascend.campaign.entities.EmailGroup;
import com.ascend.campaign.exceptions.EmailGroupNotFoundException;
import com.ascend.campaign.repositories.EmailGroupRepo;
import com.ascend.campaign.repositories.EmailRepo;
import com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmailGroupServiceTest {
    @Autowired
    EmailGroupService emailGroupService;

    @Mock
    private EmailGroupRepo emailGroupRepo;

    @Mock
    private EmailRepo emailRepo;

    private EmailGroup emailGroup;
    private EmailGroup emailGroup2;

    @Before
    public void setUp() {
        emailGroupService = new EmailGroupService(emailGroupRepo, emailRepo);
        emailGroup = new EmailGroup();
        emailGroup.setId(1L);
        emailGroup.setName("test 1");
        emailGroup.setDescription("description 1");

        emailGroup2 = new EmailGroup();
        emailGroup2.setId(2L);
        emailGroup2.setName("test 2");
        emailGroup2.setDescription("description 2");
    }

    @Test
    public void shouldCreateNewEmailGroupSuccessfully() {
        when(emailGroupRepo.saveAndFlush(any(EmailGroup.class))).thenReturn(emailGroup);
        EmailGroup emailGroupCreated = emailGroupService.createEmailGroup(emailGroup);
        assertThat(emailGroupCreated, notNullValue());
        verify(emailGroupRepo).saveAndFlush(any(EmailGroup.class));
    }

    @Test
    public void shouldReturnEmailGroupWhenGetExistingEmailGroupInDbById() {
        when(emailGroupRepo.findOne(anyLong())).thenReturn(emailGroup);
        when(emailRepo.countByEmailGroupId(anyLong())).thenReturn(1L);
        EmailGroup emailGroupResult = emailGroupService.getEmailGroup(1L);
        assertThat(emailGroupResult.getId(), is(1L));
        verify(emailGroupRepo).findOne(anyLong());
        verify(emailRepo).countByEmailGroupId(anyLong());
    }

    @Test(expected = EmailGroupNotFoundException.class)
    public void shouldReturnEmailGroupNotFoundExceptionWhenGetNonExistingEmailGroupInDbById() {
        when(emailGroupRepo.findOne(anyLong())).thenReturn(null);
        emailGroupService.getEmailGroup(1L);
        verify(emailGroupRepo).findOne(anyLong());
    }

    @Test
    public void shouldDeleteEmailGroupWhenDeleteExistingEmailGroupByIdInDb() {
        when(emailGroupRepo.findOne(anyLong())).thenReturn(emailGroup);
        doNothing().when(emailGroupRepo).delete(anyLong());
        assertThat(emailGroupService.deleteEmailGroup(1L).getId(), is(1L));
        verify(emailGroupRepo).findOne(anyLong());
    }

    @Test(expected = EmailGroupNotFoundException.class)
    public void shouldNotDeleteEmailGroupWhenDeleteNonExistingEmailGroupByIdInDb() {
        when(emailGroupRepo.findOne(anyLong())).thenReturn(null);
        doNothing().when(emailGroupRepo).delete(anyLong());
        emailGroupService.deleteEmailGroup(1L);
        verify(emailGroupRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnUpdatedEmailGroupWhenUpdateExistingEmailGroupInDbById() throws Exception {
        EmailGroup emailGroupFromDB = new EmailGroup();
        emailGroupFromDB.setName("test");
        emailGroupFromDB.setDescription("test");
        when(emailGroupRepo.findOne(anyLong())).thenReturn(emailGroupFromDB);
        EmailGroup updatedEmailGroup = new EmailGroup();
        updatedEmailGroup.setName("updated name");
        updatedEmailGroup.setDescription("updated description");
        when(emailGroupRepo.saveAndFlush(any(EmailGroup.class))).thenReturn(updatedEmailGroup);
        emailGroupService.updateEmailGroup(1L, updatedEmailGroup);
        assertEquals(emailGroupFromDB.getName(), updatedEmailGroup.getName());
        assertEquals(emailGroupFromDB.getDescription(), updatedEmailGroup.getDescription());
        verify(emailGroupRepo).findOne(anyLong());
        verify(emailGroupRepo).saveAndFlush(any(EmailGroup.class));
    }

    @Test(expected = EmailGroupNotFoundException.class)
    public void shouldNotUpdateEmailGroupWhenUpdateNonExistingEmailGroupByIdInDb() {
        when(emailGroupRepo.findOne(anyLong())).thenReturn(null);
        emailGroupService.updateEmailGroup(1L, emailGroup);
    }


    @Test
    public void shouldReturnAllEmailGroupWhenGetAllExistingEmailGroupInDb() {
        List<EmailGroup> emailGroups = Lists.newArrayList(emailGroup, emailGroup2);
        Page expectedPage = new PageImpl(emailGroups);

        when(emailGroupRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);
        when(emailRepo.countByEmailGroupId(anyLong())).thenReturn(2L);

        Page<EmailGroup> result = emailGroupService.getAllEmailGroup(
                1, 5, Sort.Direction.ASC, "id", null, null);

        assertThat(result.getTotalElements(), Matchers.not(0));

        verify(emailGroupRepo).findAll(any(), any(PageRequest.class));
        verify(emailRepo,times(2)).countByEmailGroupId(anyLong());
    }
}