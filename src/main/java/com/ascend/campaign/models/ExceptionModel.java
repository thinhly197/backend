package com.ascend.campaign.models;

import lombok.Data;

@Data
public class ExceptionModel {
    private String code;
    private String description;

    public ExceptionModel(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
