package com.ascend.campaign.constants;

import lombok.Getter;

@Getter
public enum Response {
    SUCCESS("success"),
    FAIL("fail");

    private final String content;

    Response(String content) {
        this.content = content;
    }
}
