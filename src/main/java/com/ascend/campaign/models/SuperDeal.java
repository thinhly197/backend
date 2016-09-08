package com.ascend.campaign.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
public class SuperDeal {
    @JsonProperty(value = "variant_id")
    private String variantId;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "start_period")
    private Date startPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "end_period")
    private Date endPeriod;

    @Transient
    @JsonProperty(value = "limit_account")
    private Integer limitAccount;

    @Transient
    @JsonProperty(value = "limit_per_cart")
    private Integer limitPerCart;

    @JsonProperty(value = "recommended")
    Boolean recommended;
    @JsonProperty(value = "partners")
    private List<String> partner;

    @JsonProperty(value = "payment_types")
    private List<String> paymentType;

    @JsonIgnore
    private Double dealPrice;
}
