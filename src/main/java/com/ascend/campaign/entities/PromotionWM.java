package com.ascend.campaign.entities;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.models.PromotionCondition;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "promotion_wm")
@Data
@EqualsAndHashCode(callSuper = false)
public class PromotionWM extends BasePromotion {

    @Column(name = "promotion_type", nullable = false, length = 50)
    @NotEmpty
    @Size(max = 50)
    @NotNull
    private String type;

    @Transient
    @JsonProperty(value = "condition_data")
    private PromotionCondition promotionCondition;

    @Transient
    private Boolean live;

    @Transient
    private String businessChannel = CampaignEnum.WEMALL.getContent();


}