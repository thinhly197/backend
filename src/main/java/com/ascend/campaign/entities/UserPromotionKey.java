package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
@Data
public class UserPromotionKey implements Serializable {
    public UserPromotionKey() {
    }

    public UserPromotionKey(String customerId, Long promotionId) {
        this.customerId = customerId;
        this.promotionId = promotionId;
    }

    @Column(name = "customer_id", nullable = false, length = 100)
    @JsonProperty(value = "customer_id")
    @Size(max = 100)
    private String customerId;

    @Column(name = "promotion_id", nullable = false)
    @JsonProperty(value = "promotion_id")
    private Long promotionId;
}
