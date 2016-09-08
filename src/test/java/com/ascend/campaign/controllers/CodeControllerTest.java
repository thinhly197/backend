package com.ascend.campaign.controllers;

import com.ascend.campaign.entities.Code;
import com.ascend.campaign.entities.CodeDetail;
import com.ascend.campaign.exceptions.CodeNotFoundException;
import com.ascend.campaign.exceptions.CodeTypeException;
import com.ascend.campaign.models.CodeGeneratorRequest;
import com.ascend.campaign.services.CodeGeneratorService;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CodeControllerTest {
    @Mock
    CodeGeneratorService codeGeneratorService;
    CodeDetail codeDetail;
    CodeDetail codeDetail2;
    Code code;
    List<CodeDetail> codeDetailsList;
    @InjectMocks
    private CodeController controller;
    private MockMvc mvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
        codeDetailsList = Lists.newArrayList();

        codeDetail = new CodeDetail();
        codeDetail.setId(1L);
        codeDetail.setName("Happy New Year");
        codeDetail.setAvailable(10L);
        codeDetail.setCodeRevenue(0.0);
        codeDetail.setCodeStatus("Activate");
        codeDetail.setCodeType("Single");
        codeDetail.setCodeUsed(0L);

        code = new Code();
        code.setAvailable(1L);
        code.setCode("HNY222");
        code.setStatus("Activate");
        code.setRevenue(0.0);
        code.setUse(0L);
        code.setCodeDetail(1L);

        codeDetail.setCodes(Lists.newArrayList(code));
        codeDetail2 = new CodeDetail();
        codeDetail2.setId(-1L);

        codeDetailsList.add(codeDetail);
    }

    @Test
    public void shouldReturnCreatedStatusWhenGenerateRequest() throws Exception {
        when(codeGeneratorService.codeGenerator(any(CodeGeneratorRequest.class))).thenReturn(codeDetail);

        mvc.perform(post("/api/v1/codegroups/")
                .content("{\"email\":\"setFlatDiscountData@mail.com\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(codeGeneratorService).codeGenerator(any(CodeGeneratorRequest.class));
    }

    @Test
    public void shouldReturnCodeTypeExceptionWhenGenerateRequestWithNotValidCodeType() throws Exception {
       doThrow(CodeTypeException.class).when(codeGeneratorService).codeGenerator(any(CodeGeneratorRequest.class));

        mvc.perform(post("/api/v1/codegroups/")
                .content("{\"type\":\"invalid\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldReturnFailStatusWhenGenerateBadRequest() throws Exception {
        when(codeGeneratorService.codeGenerator(any(CodeGeneratorRequest.class))).thenReturn(codeDetail2);

        mvc.perform(post("/api/v1/codegroups/")
                .content("{\"email\":\"setFlatDiscountData@mail.com\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(codeGeneratorService).codeGenerator(any(CodeGeneratorRequest.class));
    }

    @Test
    public void shouldReturnCodeDetailWhenRequest() throws Exception {
        when(codeGeneratorService.getCodeDetail(1L)).thenReturn(codeDetail);

        mvc.perform(get("/api/v1/codegroups/1"))
                .andExpect(jsonPath("$.data.name", is("Happy New Year")))
                .andExpect(status().is2xxSuccessful());

        verify(codeGeneratorService).getCodeDetail(anyLong());
    }

    @Test
    public void shouldReturnCodeListWhenRequest() throws Exception {
        Page expectedPage = new PageImpl(codeDetailsList);

        when(codeGeneratorService.getAllCodeSet(anyInt(), anyInt(), any(Sort.Direction.class),
                anyString(), anyLong(), anyString(), anyString()))
                .thenReturn(expectedPage);
        mvc.perform(get("/api/v1/codegroups/"))
                .andExpect(jsonPath("$.data.content[0].name", is(codeDetail.getName())))
                .andExpect(status().isOk());

        verify(codeGeneratorService).getAllCodeSet(anyInt(), anyInt(), any(Sort.Direction.class),
                anyString(), anyLong(), anyString(), anyString());
    }

    @Test
    public void shouldUpdateCodeDetailWhenUpdate() throws Exception {
        when(codeGeneratorService.updateCodeDetail(anyLong(), any(CodeGeneratorRequest.class))).thenReturn(codeDetail);

        mvc.perform(put("/api/v1/codegroups/1")
                .content("{\"email\":\"setFlatDiscountData@mail.com\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.name", is("Happy New Year")))
                .andExpect(jsonPath("$.data.id", is(1)))
                        //.andDo(print())
                .andExpect(status().is2xxSuccessful());

        verify(codeGeneratorService).updateCodeDetail(anyLong(), any(CodeGeneratorRequest.class));
    }

    @Test
    public void shouldReturnAllCodesWhenRequest() throws Exception {
        List<Code> codeList = new ArrayList<>();
        codeList.add(code);
        Page expectedPage = new PageImpl(codeList);

        when(codeGeneratorService.getAllCode(anyInt(), anyInt(), any(Sort.Direction.class), anyString(), anyString()))
                .thenReturn(expectedPage);

        mvc.perform(get("/api/v1/codes/"))
                .andExpect(status().isOk());

        verify(codeGeneratorService).getAllCode(anyInt(), anyInt(),
                any(Sort.Direction.class), anyString(), anyString());
    }

    @Test
    public void shouldReturnAllCodesInCodeListWhenRequest() throws Exception {
        List<Code> codeList = new ArrayList<>();
        codeList.add(code);
        Page expectedPage = new PageImpl(codeList);

        when(codeGeneratorService.getCodePage(anyLong(), anyInt(), anyInt(), any(Sort.Direction.class), anyString()))
                .thenReturn(expectedPage);

        mvc.perform(get("/api/v1/codegroups/1/codes"))
                .andExpect(jsonPath("$.data.content.[*].code", hasItem(endsWith("HNY222"))))
                        //.andDo(print())
                .andExpect(status().isOk());

        verify(codeGeneratorService).getCodePage(anyLong(),
                anyInt(), anyInt(), any(Sort.Direction.class), anyString());
    }

    @Test
    public void shouldDeleteCodeGroupWhenDeleteCodeGroupByIdAndExistingCodeGroupInDb() throws Exception {
        when(codeGeneratorService.deleteCode(anyLong())).thenReturn(codeDetail);

        mvc.perform(delete("/api/v1/codegroups/1")).andDo(print())
                .andExpect(jsonPath("$.data.name", is("Happy New Year")))
                .andExpect(status().is2xxSuccessful());

        verify(codeGeneratorService).deleteCode(anyLong());
    }

    @Test
    public void shouldReturnCodeWhenRequestById() throws Exception {
        when(codeGeneratorService.getCode(anyLong())).thenReturn(code);

        mvc.perform(get("/api/v1/codes/1/"))
                .andExpect(status().isOk());

        verify(codeGeneratorService).getCode(anyLong());
    }

    @Test
    public void shouldNotFoundWhenRequestNonExistingCode() throws Exception {
        doThrow(CodeNotFoundException.class).when(codeGeneratorService).getCode(anyLong());

        mvc.perform(get("/api/v1/codes/1/"))
                .andExpect(status().isNotFound());

        verify(codeGeneratorService).getCode(anyLong());
    }

    @Test
    public void shouldReturnResultWhenSearchByCode() throws Exception {
        List<Long> codeList = new ArrayList<>();

        when(codeGeneratorService.findPromotionFromCode(anyString())).thenReturn(codeList);

        mvc.perform(get("/api/v1/codes/search")
                .param("code", "HNY"))
                .andExpect(status().isOk());

        verify(codeGeneratorService).findPromotionFromCode(anyString());
        verify(codeGeneratorService, never()).searchCodeDetailByName(anyString(),
                anyInt(), anyInt(), any(Sort.Direction.class), anyString());
    }

    @Test
    public void shouldReturnResultWhenSearchByCodeSet() throws Exception {
        Page expectedPage = new PageImpl(codeDetailsList);

        when(codeGeneratorService.searchCodeDetailByName(anyString(), anyInt(), anyInt(), any(Sort.Direction.class),
                anyString())).thenReturn(expectedPage);

        mvc.perform(get("/api/v1/codes/search")
                .param("codeset", "HNY"))
                .andExpect(status().isOk());

        verify(codeGeneratorService, never()).findPromotionFromCode(anyString());
        verify(codeGeneratorService).searchCodeDetailByName(anyString(),
                anyInt(), anyInt(), any(Sort.Direction.class), anyString());
    }

    @Test
    public void shouldReturnBadResultIfNotHaveParam() throws Exception {
        mvc.perform(get("/api/v1/codes/search"))
                .andExpect(status().isBadRequest());

        verify(codeGeneratorService, never()).findPromotionFromCode(anyString());
        verify(codeGeneratorService, never()).searchCodeDetailByName(anyString(),
                anyInt(), anyInt(), any(Sort.Direction.class), anyString());
    }

}