package com.ascend.campaign.constants;

import lombok.Getter;

@Getter
public enum DrlOperator {
    DRL_OPERATOR_NONE(""),
    DRL_OPERATOR_OR("||"),
    DRL_OPERATOR_AND("&&"),
    DRL_OPERATOR_EQUALS("=="),
    DRL_OPERATOR_NOT_EQUALS("!=");

    private final String signal;

    DrlOperator(String signal) {
        this.signal = signal;
    }
}
