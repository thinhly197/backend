package com.ascend.campaign.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "flashsale_variant")
@Getter
@Setter
public class FlashSaleVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    private Long id;

    @JsonProperty(value = "variant_id")
    @Column(name = "variant_id", nullable = false)
    @NotNull
    private String variantId;

    @JsonProperty(value = "promotion_price")
    @Column(name = "promotion_price", nullable = false)
    @NotNull
    private Double promotionPrice;

    @JsonProperty(value = "discount_percent")
    @Column(name = "discount_percent", nullable = false)
    @NotNull
    private Double discountPercent;

    @JsonProperty(value = "limit_quantity")
    private Long limitQuantity;

    @JsonProperty(value = "is_available")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;


    @PrePersist
    void preInsertFlashSaleProduct() {
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
    }

    @PreUpdate
    void preUpdateFlashSaleProduct() {
        if (this.isAvailable == null) {
            this.isAvailable = true;
        }
    }

    @ManyToOne
    @JoinColumn(name = "product_key", nullable = false)
    @JsonIgnore
    private FlashSaleProduct productKey;

}
