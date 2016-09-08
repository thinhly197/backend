package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Code;
import com.ascend.campaign.entities.CodeDetail;
import com.ascend.campaign.exceptions.CodeNotFoundException;
import com.ascend.campaign.models.CodeGeneratorRequest;
import com.ascend.campaign.repositories.CodeDetailRepo;
import com.ascend.campaign.repositories.CodeRepo;
import com.ascend.campaign.utils.CodeGenerateUtil;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class CodeGeneratorServiceTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Autowired
    CodeGeneratorService codeGeneratorService;
    CodeGeneratorRequest codeGeneratorRequest;
    CodeGeneratorRequest codeGeneratorRequest2;
    CodeGeneratorRequest codeGeneratorRequest3;
    CodeGeneratorRequest codeGeneratorRequest4;
    CodeGeneratorRequest codeGeneratorRequest5;
    CodeGeneratorRequest codeGeneratorRequest6;
    @Mock
    private CodeRepo codeRepo;
    @Mock
    private CodeDetailRepo codeDetailRepo;
    @Mock
    private CodeGenerateUtil codeGenerateUtil;
    private CodeDetail codeDetail;
    private List<Code> codes;
    private List<CodeDetail> codeDetails;
    private Code code;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        codeGeneratorService = new CodeGeneratorService(codeRepo, codeDetailRepo, codeGenerateUtil);

        codeGeneratorRequest = new CodeGeneratorRequest();
        codeGeneratorRequest.setName("Happy New Year");
        codeGeneratorRequest.setLimitOfUse(20L);
        codeGeneratorRequest.setType(CampaignEnum.SINGLE.getContent());
        codeGeneratorRequest.setFormat(CampaignEnum.RANDOM.getContent());
        codeGeneratorRequest.setPrefix("HNY");
        codeGeneratorRequest.setSuffixLength(3);

        codeGeneratorRequest2 = new CodeGeneratorRequest();
        codeGeneratorRequest2.setName("Happy New Year");
        codeGeneratorRequest2.setLimitOfUse(20L);
        codeGeneratorRequest2.setType(CampaignEnum.SINGLE.getContent());
        codeGeneratorRequest2.setFormat(CampaignEnum.FIX.getContent());
        codeGeneratorRequest2.setCode("HNY");

        codeGeneratorRequest3 = new CodeGeneratorRequest();
        codeGeneratorRequest3.setName("Happy New Year");
        codeGeneratorRequest3.setQuantity(12L);
        codeGeneratorRequest3.setType(CampaignEnum.UNIQUE.getContent());
        codeGeneratorRequest3.setPrefix("HNY");
        codeGeneratorRequest3.setSuffixLength(3);

        codeGeneratorRequest4 = new CodeGeneratorRequest();
        codeGeneratorRequest4.setName("Happy New Year");
        codeGeneratorRequest4.setQuantity(950L);
        codeGeneratorRequest4.setType(CampaignEnum.UNIQUE.getContent());
        codeGeneratorRequest4.setPrefix("HNY");
        codeGeneratorRequest4.setSuffixLength(2);

        codeGeneratorRequest5 = new CodeGeneratorRequest();
        codeGeneratorRequest5.setName("Happy New Year");
        codeGeneratorRequest5.setQuantity(100L);
        codeGeneratorRequest5.setType(CampaignEnum.UNIQUE.getContent());
        codeGeneratorRequest5.setPrefix("HNY");
        codeGeneratorRequest5.setSuffixLength(1);

        codeGeneratorRequest6 = new CodeGeneratorRequest();
        codeGeneratorRequest6.setName("Happy New Year");
        codeGeneratorRequest6.setQuantity(20L);
        codeGeneratorRequest6.setType(CampaignEnum.UNIQUE.getContent());
        codeGeneratorRequest6.setPrefix("HNY");
        codeGeneratorRequest6.setSuffixLength(1);

        codeDetail = new CodeDetail();
        codeDetail.setAvailable(3L);
        codeDetail.setName("Happy New Year");
        codeDetail.setCodeRevenue(0.0);
        codeDetail.setCodeStatus(CampaignEnum.ACTIVATE.getContent());
        codeDetail.setCodeType(CampaignEnum.SINGLE.getContent());
        codeDetail.setId(1L);

        codes = new ArrayList<>();
        codes.add(new Code());

        codeDetails = new ArrayList<>();
        codeDetails.add(codeDetail);

    }

    @Test
    public void shouldGenerateCodeSuccessfullyWhenRequest() {
        when(codeDetailRepo.save(any(CodeDetail.class))).thenReturn(codeDetail);
        when(codeGenerateUtil.genSuffixCode(anyString(), anyLong(), anyInt(), anyLong(), anySet())).thenReturn(codes);

        //generate Single Random
        CodeDetail result = codeGeneratorService.codeGenerator(codeGeneratorRequest);
        assertThat(result.getId(), not(-1L));

        //generate Single Fix
        CodeDetail result2 = codeGeneratorService.codeGenerator(codeGeneratorRequest2);
        assertThat(result2.getId(), not(-1L));

        //generate Unique
        CodeDetail result3 = codeGeneratorService.codeGenerator(codeGeneratorRequest3);
        assertThat(result3.getId(), not(-1L));

        verify(codeDetailRepo, times(6)).save(any(CodeDetail.class));
    }

    @Test
    public void shouldReturnErrorId() {
        when(codeDetailRepo.save(any(CodeDetail.class))).thenReturn(codeDetail);
        when(codeGenerateUtil.genSuffixCode(anyString(), anyLong(), anyInt(), anyLong(), anySet()))
                .thenReturn(new ArrayList<>());

        CodeDetail result = codeGeneratorService.codeGenerator(codeGeneratorRequest);
        assertThat(result.getId(), is(-1L));

        //generate code more than 80% of prob
        CodeDetail result1 = codeGeneratorService.codeGenerator(codeGeneratorRequest4);
        assertThat(result1.getId(), is(-1L));

        //generate code more than prob
        CodeDetail result2 = codeGeneratorService.codeGenerator(codeGeneratorRequest5);
        assertThat(result2.getId(), is(-1L));

        //generate existing code more than 50% of expect
        CodeDetail result3 = codeGeneratorService.codeGenerator(codeGeneratorRequest6);
        assertThat(result3.getId(), is(-1L));

        verify(codeDetailRepo, times(4)).save(any(CodeDetail.class));
    }


    @Test
    public void shouldReturnErrorIdIfCreateSameCode() {
        when(codeDetailRepo.save(any(CodeDetail.class))).thenReturn(codeDetail);
        Code code = new Code();
        code.setCode("HNY");
        codes.add(code);
        when(codeRepo.findAll()).thenReturn(codes);

        //generate existing code
        CodeDetail result2 = codeGeneratorService.codeGenerator(codeGeneratorRequest2);
        assertThat(result2.getId(), is(-1L));

        verify(codeDetailRepo, times(1)).save(any(CodeDetail.class));
    }

    @Test
    public void shouldReturnOnlyCodeDetailWhenRequest() {
        when(codeDetailRepo.findOne(anyLong())).thenReturn(codeDetail);

        CodeDetail result = codeGeneratorService.getCodeDetail(1L);
        assertThat(result.getId(), Matchers.not(-1L));

        verify(codeDetailRepo).findOne(anyLong());
    }

    @Test
    public void shouldReturnAllCodeSet() {
        Page expectedPage = new PageImpl(codeDetails);
        when(codeDetailRepo.findAll(any(), any(PageRequest.class))).thenReturn(expectedPage);

        Page<CodeDetail> result = codeGeneratorService.getAllCodeSet(1, 5, Sort.Direction.ASC, "id", null, null, null);
        assertThat(result.getTotalElements(), Matchers.not(0));

        verify(codeDetailRepo).findAll(any(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnCodeDetailUpdated() {
        CodeGeneratorRequest codeDetailOriginal = new CodeGeneratorRequest();
        codeDetailOriginal.setName("Happy New Year");

        CodeDetail codeDetail = new CodeDetail();
        codeDetail.setName("HHH");

        when(codeDetailRepo.save(any(CodeDetail.class))).thenReturn(codeDetail);
        when(codeDetailRepo.findOne(anyLong())).thenReturn(codeDetail);

        CodeDetail result = codeGeneratorService.updateCodeDetail(1L, codeDetailOriginal);
        assertThat(result.getName(), is("Happy New Year"));

        verify(codeDetailRepo).findOne(anyLong());
        verify(codeDetailRepo).save(any(CodeDetail.class));
    }

    @Test
    public void shouldReturnIdOfCodeDetail() {
        code = new Code();
        code.setCodeDetail(1L);
        when(codeRepo.findByCode(anyString())).thenReturn(code);

        assertThat(codeGeneratorService.isCodeCanApply("abc"), is(1L));

        verify(codeRepo).findByCode(anyString());
    }

    @Test
    public void shouldReturnFalseIfCodeCantApply() {
        when(codeRepo.findByCode(anyString())).thenReturn(code);

        assertThat(codeGeneratorService.isCodeCanApply("abc"), is(0L));

        verify(codeRepo).findByCode(anyString());
    }

    @Test
    public void shouldReturnCodeWhenRequestId() {
        Code code = new Code();
        code.setId(1L);
        when(codeRepo.findById(anyLong())).thenReturn(code);

        assertThat(codeGeneratorService.getCode(1L).getId(), is(1L));

        verify(codeRepo).findById(anyLong());
    }

    @Test(expected = CodeNotFoundException.class)
    public void shouldReturnNotFoundWhenRequestNonExistingCodeId() {
        when(codeRepo.findById(anyLong())).thenReturn(null);

        codeGeneratorService.getCode(1L);

        verify(codeRepo).findById(anyLong());
    }

    @Test
    public void shouldNotReturnCodeWhenRequestId() {
        Code code = new Code();
        code.setId(1L);
        when(codeRepo.findById(anyLong())).thenReturn(code);

        assertThat(codeGeneratorService.getCode(1L).getId(), is(1L));

        verify(codeRepo).findById(anyLong());
    }

    @Test
    public void shouldReturnPromotionIdWhenRequestByCodeString() {
        List<Long> promotionList = new ArrayList<>();
        promotionList.add(1L);
        when(codeRepo.findPromotionByCode(anyString())).thenReturn(promotionList);

        assertThat(codeGeneratorService.findPromotionFromCode("code").get(0), is(1L));

        verify(codeRepo).findPromotionByCode(anyString());
    }

    @Test
    public void shouldReturnCodesInCodeSet() {
        Code code = new Code();
        code.setId(1L);
        List<Code> promotionList = new ArrayList<>();
        promotionList.add(code);
        Page expectedPage = new PageImpl(promotionList);
        when(codeRepo.findByCodeDetail(anyLong(), any(PageRequest.class)))
                .thenReturn(expectedPage);

        assertThat(codeGeneratorService.getCodePage(1L, 1, 1, Sort.Direction.ASC, "name").getContent().get(0).getId(),
                is(1L));

        verify(codeRepo).findByCodeDetail(anyLong(), any(PageRequest.class));
    }

    @Test
    public void shouldReturnSearchResult() {

        Page expectedPage = new PageImpl(codeDetails);
        when(codeDetailRepo.findByNameLike(anyString(), any(Pageable.class))).thenReturn(expectedPage);

        assertThat(codeGeneratorService.searchCodeDetailByName("name", 1, 1, Sort.Direction.ASC, "name")
                .getContent().get(0).getId(), is(1L));

        verify(codeDetailRepo).findByNameLike(anyString(), any(Pageable.class));
    }

    @Test
    public void shouldReturnCode() {
        Code code = new Code();
        code.setId(1L);
        List<Code> codeList = new ArrayList<>();
        codeList.add(code);
        Page expectedPage = new PageImpl(codeList);
        when(codeRepo.findByCode(anyString(), any(Pageable.class))).thenReturn(expectedPage);
        when(codeRepo.findAll(any(Pageable.class))).thenReturn(expectedPage);

        assertThat(codeGeneratorService.getAllCode(1, 1, Sort.Direction.ASC, "name", "name")
                .getContent().get(0).getId(), is(1L));

        assertThat(codeGeneratorService.getAllCode(1, 1, Sort.Direction.ASC, "name", "")
                .getContent().size(), not(0));

        verify(codeRepo).findByCode(anyString(), any(Pageable.class));
    }
}
