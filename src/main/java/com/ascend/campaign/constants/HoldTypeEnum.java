package com.ascend.campaign.constants;

import lombok.Getter;

@Getter
public enum HoldTypeEnum {
    COD("cod"),
    ONLINE("online"),
    OFFLINE("offline");

    private final String content;

    HoldTypeEnum(String content) {
        this.content = content;
    }
}
