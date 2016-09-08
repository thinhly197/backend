package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.EmailGroup;
import com.ascend.campaign.exceptions.EmailGroupNotFoundException;
import com.ascend.campaign.services.EmailGroupService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmailGroupControllerTest {
    @Mock
    EmailGroupService emailGroupService;

    @InjectMocks
    private EmailGroupController controller;

    private MockMvc mvc;

    EmailGroup emailGroup;
    EmailGroup emailGroup2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        emailGroup = new EmailGroup();
        emailGroup.setName("testName");
        emailGroup.setId(3L);

        emailGroup2 = new EmailGroup();
        emailGroup2.setName("testName2");
        emailGroup2.setId(4L);
    }

    @Test
    public void shouldReturnEmailGroupListWhenGetAllExistingEmailGroupInDb() throws Exception {
        List<EmailGroup> emailGroups = Lists.newArrayList(emailGroup, emailGroup2);
        Page expectedPage = new PageImpl(emailGroups);

        when(emailGroupService.getAllEmailGroup(anyInt(), anyInt(), any(Sort.Direction.class),
                anyString(), anyLong(), anyString())).thenReturn(expectedPage);

        mvc.perform(get("/api/v1/emailgroups"))
                .andExpect(jsonPath("$.data.content[0].name", is(emailGroup.getName())))
                .andExpect(jsonPath("$.data.content[1].name", is(emailGroup2.getName())))
                .andExpect(status().isOk());

        verify(emailGroupService).getAllEmailGroup(anyInt(), anyInt(), any(Sort.Direction.class),
                anyString(), anyLong(), anyString());
    }


    @Test
    public void shouldReturnCreatedStatusWhenCreateEmailGroup() throws Exception {
        when(emailGroupService.createEmailGroup(any(EmailGroup.class))).thenReturn(emailGroup);
        mvc.perform(post("/api/v1/emailgroups/")
                .content("{\"name\":\"testname\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(emailGroupService).createEmailGroup(any(EmailGroup.class));
    }

    @Test
    public void shouldReturnEmailGroupWhenGetExistingEmailGroupById() throws Exception {
        when(emailGroupService.getEmailGroup(3L)).thenReturn(emailGroup);
        mvc.perform(get("/api/v1/emailgroups/3"))
                .andExpect(jsonPath("$.data.id", is(3)))
                .andExpect(jsonPath("$.data.name", is(emailGroup.getName())))
                .andExpect(status().isOk());

        verify(emailGroupService).getEmailGroup(anyLong());
    }

    @Test
    public void shouldReturnNotFoundEmailGroupWhenGetNonExistingEmailGroupById() throws Exception {
        doThrow(EmailGroupNotFoundException.class).when(emailGroupService).getEmailGroup(anyLong());

        mvc.perform(get("/api/v1/emailgroups/2"))
                .andExpect(status().isNotFound());

        verify(emailGroupService).getEmailGroup(anyLong());
    }

    @Test
    public void shouldDeleteEmailGroupWhenDeleteExistingEmailGroupById() throws Exception {
        when(emailGroupService.deleteEmailGroup(anyLong())).thenReturn(emailGroup);

        mvc.perform(delete("/api/v1/emailgroups/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(3)))
                .andExpect(status().isOk());

        verify(emailGroupService).deleteEmailGroup(anyLong());
    }

    @Test
    public void shouldReturnEmailGroupUpdatedWhenUpdateExistingEmailGroupById() throws Exception {
        emailGroup.setName("UpdatedName");

        when(emailGroupService.updateEmailGroup(anyLong(), any(EmailGroup.class))).thenReturn(emailGroup);

        mvc.perform(put("/api/v1/emailgroups/3")
                .content("{\"name\":\"UpdatedName\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(3)))
                .andExpect(status().isOk());

        verify(emailGroupService).updateEmailGroup(anyLong(), any(EmailGroup.class));
    }


}