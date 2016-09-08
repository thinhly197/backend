package com.ascend.campaign.controllers;

import com.ascend.campaign.constants.Errors;
import com.ascend.campaign.models.ResponseModel;
import com.ascend.campaign.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        log.error("content={\"activity\":\"handleExceptionInternal\", \"msg\":{\"error_message\":\"{}\"}",
                ex.getMessage(), ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleUnexpected(Throwable throwable) {
        log.error("content={\"activity\":\"Unexpected Error\", \"msg\":{\"error_message\":\"{}\"}",
                throwable.getMessage(), throwable);

        return new ResponseEntity<>(new ResponseModel(Errors.UNEXPECTED_ERROR.getErrorDesc()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}