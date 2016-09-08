package com.ascend.campaign.models;

import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class ResponseModel {

    final String message;
    final Object data;

    public ResponseModel(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public ResponseModel(String message) {
        this.message = message;
        this.data = "";
    }

    public HttpEntity<ResponseModel> build(HttpStatus status) {
        return new ResponseEntity<>(new ResponseModel(this.message, this.data), status);
    }
}