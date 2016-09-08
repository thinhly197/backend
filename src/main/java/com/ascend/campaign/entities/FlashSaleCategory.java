package com.ascend.campaign.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Proxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "flashsale_category")

public class FlashSaleCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_key")
    @JsonIgnore
    private FlashSaleProduct flashSaleProduct;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

}
