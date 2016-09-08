package com.ascend.campaign.entities;

import com.ascend.campaign.constants.CampaignEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.text.DecimalFormat;
import java.util.List;

@Entity
@Table(name = "code_detail")
@Data
@EqualsAndHashCode(callSuper = false)
public class CodeDetail extends BaseEntity {
    @Column(name = "name", length = 200)
    @JsonProperty(value = "name")
    private String name;

    @Column(name = "type", length = 15)
    @JsonProperty(value = "type")
    private String codeType;

    @Column(name = "format")
    @JsonProperty(value = "format")
    private String codeFormat;

    @Column(name = "format_prefix")
    @JsonProperty(value = "prefix")
    private String codeFormatPrefix;

    @Column(name = "format_suffix")
    @JsonProperty(value = "suffix_length")
    private Integer codeFormatSuffix;

    @Column(name = "status", length = 10)
    @JsonProperty(value = "status")
    @Size(max = 10)
    private String codeStatus;

    // limit of uses
    @Column(name = "total")
    @JsonProperty(value = "total")
    private Long available;

    @Column(name = "type_of_limitation")
    @JsonProperty(value = "type_of_limitation")
    private String typeOfLimitation;

    // limit of times/users
    @Column(name = "time_per_user")
    @JsonProperty(value = "limit_of_time_or_user")
    private Integer limitOfTimeOrUser;

    @Column(name = "used")
    @JsonProperty(value = "used")
    private Long codeUsed;

    @Column(name = "revenue")
    @JsonProperty(value = "revenue")
    private Double codeRevenue;

    @Column(name = "promotion_id")
    @JsonProperty(value = "promotion_id")
    private Long promotionId;

    @Transient
    List<Code> codes;

    public void refreshCode() {
        int count = 0;
        Double revenue = 0.0;
        for (int i = 0; i < codes.size(); i++) {
            if (codes.get(i).getStatus().equals(CampaignEnum.ACTIVATE.getContent())) {
                count += codes.get(i).getUse();
                revenue += codes.get(i).getRevenue();
            }
        }
        setCodeUsed((long) count);
        setCodeRevenue(Double.parseDouble(new DecimalFormat("#.##").format(revenue)));
    }
}
