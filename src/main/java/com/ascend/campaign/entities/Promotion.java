package com.ascend.campaign.entities;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.constants.PromotionTypeEnum;
import com.ascend.campaign.models.DetailData;
import com.ascend.campaign.models.PromotionCondition;
import com.ascend.campaign.validators.VerifyValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "promotion")
@Data
@EqualsAndHashCode(callSuper = false)
public class Promotion extends BasePromotion {

    @Transient
    @JsonProperty(value = "detail_data")
    private DetailData promotionData;

    @Transient
    @JsonProperty(value = "condition_data")
    private PromotionCondition promotionCondition;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "promotion_type", nullable = false, length = 50)
    @NotEmpty
    @Size(max = 50)
    @NotNull
    @VerifyValue(PromotionTypeEnum.class)
    private String type;

    @Lob
    @Column(name = "detail_data")
    @JsonIgnore
    private String detailData;

    @Transient
    private Boolean live;

    @Transient
    @JsonProperty(value = "filter_status")
    private String filterStatus;

    @Transient
    @JsonProperty(value = "pending_status")
    private String pendingStatus;

    @Transient
    private String businessChannel = CampaignEnum.ITRUEMART.getContent();


}