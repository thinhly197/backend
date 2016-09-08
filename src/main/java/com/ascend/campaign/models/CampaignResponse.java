package com.ascend.campaign.models;

import com.ascend.campaign.entities.Campaign;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class CampaignResponse extends Campaign {
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "min_period")
    private Date minPeriodPromotion;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "max_period")
    private Date maxPeriodPromotion;
}