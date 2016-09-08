package com.ascend.campaign.models;

import lombok.Data;

@Data
public class CodeGeneratorRequest {
    String name;
    Long limitOfUse;
    Long quantity;
    String type;
    String format;
    String prefix;
    Integer suffixLength;
    String code;
    Long promotionId;
    String typeOfLimitation;
    Integer limitOfTimeOrUser;
}
