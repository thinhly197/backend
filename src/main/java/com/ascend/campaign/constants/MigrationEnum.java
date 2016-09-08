package com.ascend.campaign.constants;

import lombok.Getter;

@Getter
public enum MigrationEnum {
    MIGRATION_2016S2("migration_2016S2_freebie");

    private final String content;

    MigrationEnum(String content) {
        this.content = content;
    }
}
