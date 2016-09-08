package com.ascend.campaign.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Data
@Table(name = "promotion_task")
public class PromotionTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    private Long id;

    @Column(name = "promotion_id", nullable = true)
    private Long promotionId;

    @Column(name = "is_start", nullable = true, columnDefinition = "TINYINT(1)")
    private Boolean isStart;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "trigger_at", nullable = true)
    private Date triggerAt;
}
