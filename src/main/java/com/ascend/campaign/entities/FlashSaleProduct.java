package com.ascend.campaign.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity
@Table(name = "flashsale_product")
@Getter
@Setter
public class FlashSaleProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    private Long id;


    @JsonProperty(value = "product_key")
    @Column(name = "product_key", nullable = false)
    private String productKey;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "flashSaleProduct", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FlashSaleCategory> flashsaleCategories;


    @JsonProperty(value = "category_ids")
    @Transient
    private List<String> categoryIds;

    @JsonProperty(value = "flashsale_variants")
    //@Transient
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "productKey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashSaleVariant> flashsaleVariants;

    @JsonIgnore
    @Column(name = "min_promotion_price", nullable = false)
    private Double minPromotionPrice;

    @JsonIgnore
    @Column(name = "max_promotion_price", nullable = false)
    private Double maxPromotionPrice;

    @JsonIgnore
    @Column(name = "min_discount_percent", nullable = false)
    private Double minDiscountPercent;

    @JsonIgnore
    @Column(name = "max_discount_percent", nullable = false)
    private Double maxDiscountPercent;

    @JsonProperty(value = "is_available")
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "flashsale_id", nullable = false)
    @JsonIgnore
    private FlashSale flashSale;

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


}
