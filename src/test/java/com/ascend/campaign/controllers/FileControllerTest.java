package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.entities.Email;
import com.ascend.campaign.exceptions.CodeNotFoundException;
import com.ascend.campaign.exceptions.EmailDuplicateException;
import com.ascend.campaign.exceptions.EmailFormatException;
import com.ascend.campaign.services.FileService;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileControllerTest {
    @Mock
    FileService fileService;
    @InjectMocks
    private FileController controller;
    private MockMvc mvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnOKWhenImportSuccess() throws Exception {
        when(fileService.importData(anyLong(), any(MultipartFile.class)))
                .thenReturn(CampaignEnum.SUCCESSFULLY.getContent());

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", null, "a@mail.com".getBytes());

        mvc.perform(fileUpload("/api/v1/files/1/import")
                .file(file))
                .andExpect(status().isOk());

        verify(fileService).importData(anyLong(), any(MultipartFile.class));
    }

    @Test
    public void shouldReturnBadRequestWhenImportWithInvalidEmail() throws Exception {
        when(fileService.importData(anyLong(), any(MultipartFile.class))).thenThrow(EmailFormatException.class);

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", null, "abail.com".getBytes());

        mvc.perform(fileUpload("/api/v1/files/1/import")
                .file(file))
                .andExpect(status().isBadRequest());

        verify(fileService).importData(anyLong(), any(MultipartFile.class));
    }

    @Test
    public void shouldReturnBadRequestWhenImportWithDuplicationEmail() throws Exception {
        when(fileService.importData(anyLong(), any(MultipartFile.class))).thenThrow(EmailDuplicateException.class);

        MockMultipartFile file = new MockMultipartFile("file", "test.csv", null, "a@mail.com".getBytes());

        mvc.perform(fileUpload("/api/v1/files/1/import")
                .file(file))
                .andExpect(status().isBadRequest());

        verify(fileService).importData(anyLong(), any(MultipartFile.class));
    }

    @Test
    public void shouldReturnCSVFileWhenExportEmailListByEmailGroupIdSuccessfully() throws Exception {
        ByteArrayOutputStream csvResult = new ByteArrayOutputStream();
        Writer outputWriter = new OutputStreamWriter(csvResult);
        CsvWriterSettings writerSettings = new CsvWriterSettings();
        writerSettings.setQuoteAllFields(true);
        writerSettings.setHeaders("Email");
        CsvWriter writer = new CsvWriter(outputWriter, writerSettings);
        writer.writeHeaders();
        Email email1 = new Email();
        email1.setEmail("Test@gmail.com");
        email1.setEmailGroupId(1L);
        List<Email> codeVIPList = Arrays.asList(email1);
        codeVIPList.forEach(x -> writer.writeRow(x.getEmail()));
        writer.close();

        when(fileService.exportData(anyLong()))
                .thenReturn(csvResult);

        mvc.perform(get("/api/v1/files/1/export")).andDo(print())
                .andExpect(status().isOk());

        verify(fileService).exportData(anyLong());
    }

    @Test
    public void shouldReturnCodeNotFoundExceptionWhenExportEmailListByEmailGroupIdNotExistingInDB() throws Exception {

        doThrow(CodeNotFoundException.class).when(fileService).exportData(anyLong());

        mvc.perform(get("/api/v1/files/1/export")).andDo(print())
                .andExpect(jsonPath("$.message", is("Code not found !!")))
                .andExpect(status().isNotFound());

    }

    @Test
    public void shouldReturnCSVTemplateFileWhenExportEmailCodeTemplateSuccessfully() throws Exception {
        ByteArrayOutputStream csvResult = new ByteArrayOutputStream();
        Writer outputWriter = new OutputStreamWriter(csvResult);

        CsvWriterSettings writerSettings = new CsvWriterSettings();
        writerSettings.setQuoteAllFields(true);
        writerSettings.setHeaders("Email");

        CsvWriter writer = new CsvWriter(outputWriter, writerSettings);
        writer.writeHeaders();
        writer.close();

        when(fileService.exportVIPEmailTemplate())
                .thenReturn(csvResult);

        mvc.perform(get("/api/v1/files/template/export")).andDo(print())
                .andExpect(status().isOk());

        verify(fileService).exportVIPEmailTemplate();
    }
}