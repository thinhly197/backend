package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "order_promotion")
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderPromotion extends BaseEntity {
    @Column(name = "order_id", nullable = true, length = 100)
    @JsonProperty(value = "order_id")
    @Size(max = 100)
    private String orderId;

    @Column(name = "customer_id", nullable = true, length = 100)
    @JsonProperty(value = "customer_id")
    @Size(max = 100)
    private String customerId;

    @Column(name = "promotion_id", nullable = true)
    @JsonProperty(value = "promotion_id")
    private Long promotionId;

    @Column(name = "discount_value", nullable = true, columnDefinition = "DOUBLE(10,2)")
    @JsonProperty(value = "discount_value")
    private Double discountValue;

    @Column(name = "hold_type", nullable = true, length = 10)
    @JsonProperty(value = "hold_type")
    @Size(max = 10)
    private String holdType;

    @Column(name = "hold_time", nullable = true)
    @JsonProperty(value = "hold_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date holdTime;

    @Column(name = "hold_at", nullable = true)
    @JsonProperty(value = "hold_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date holdAt;
}