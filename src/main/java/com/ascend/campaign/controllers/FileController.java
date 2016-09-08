package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.constants.Response;
import com.ascend.campaign.exceptions.CodeNotFoundException;
import com.ascend.campaign.exceptions.EmailDuplicateException;
import com.ascend.campaign.exceptions.EmailFormatException;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.services.FileService;
import lombok.NonNull;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    @NonNull
    FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping(value = "/{emailGroupId}/import", headers = ("content-type=multipart/*"),
            method = RequestMethod.POST)
    public HttpEntity<ResponseModel> importCodeVIP(@PathVariable Long emailGroupId,
                                                   @RequestParam("file") MultipartFile file) throws Exception {
        return new ResponseModel(Response.SUCCESS.getContent(), fileService.importData(emailGroupId, file))
                .build(HttpStatus.OK);
    }

    @RequestMapping(value = "/{emailGroupId}/export", method = RequestMethod.GET)
    public void exportCodeVIP(HttpServletResponse response, @PathVariable Long emailGroupId) throws Exception {
        String fileName = String.format("%s_vip_mail.csv", DateTime.now().toString("yyyyMMddHHmmss"));
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setContentType("text/csv; charset=utf-8");

        ByteArrayOutputStream byteArrayOutputStream = fileService.exportData(emailGroupId);

        OutputStream outputStream = response.getOutputStream();
        byteArrayOutputStream.writeTo(outputStream);
        outputStream.close();
    }

    @RequestMapping(value = "/template/export", method = RequestMethod.GET)
    public void exportTemplateEmailCodeTemplate(HttpServletResponse response)
            throws Exception {
        String fileName = String.format("template_vip_email.csv");
        response.setContentType("text/csv; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        ByteArrayOutputStream byteArrayOutputStream = fileService.exportVIPEmailTemplate();

        OutputStream outputStream = response.getOutputStream();
        byteArrayOutputStream.writeTo(outputStream);
        outputStream.close();
    }

    @ExceptionHandler(value = EmailFormatException.class)
    public HttpEntity<ResponseModel> handleEmailFormatException() {
        return new ResponseModel(Errors.EMAIL_FORMAT_INVALID.getErrorDesc()).build(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = EmailDuplicateException.class)
    public HttpEntity<ResponseModel> handleEmailDuplicateException() {
        return new ResponseModel(Errors.CODE_VIP_EMAIL_DUPLICATE.getErrorCode()).build(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = CodeNotFoundException.class)
    public HttpEntity<ResponseModel> handleCodeNotFoundException() {
        return new ResponseModel(Errors.CODE_NOT_FOUND.getErrorDesc()).build(HttpStatus.NOT_FOUND);
    }
}
