package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "user_promotion")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserPromotion extends BaseAuditEntity {
    @EmbeddedId
    @JsonProperty(value = "user_promotion_id")
    private UserPromotionKey userPromotionKey;

    @Column(name = "exe_limit", nullable = true)
    @JsonProperty(value = "exe_limit")
    private Integer exeLimit;

    @Column(name = "exe_time", nullable = true)
    @JsonProperty(value = "exe_time")
    private Integer exeTime;

    @PrePersist
    @PreUpdate
    void preTransactionUserPromotion() {
        this.exeLimit = this.exeLimit == null ? 0 : this.exeLimit;
        this.exeTime = this.exeTime == null ? 0 : this.exeTime;
    }
}