package com.ascend.campaign.entities;

import com.ascend.campaign.models.DetailData;
import com.ascend.campaign.models.PromotionCondition;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "PendingPromotion")
@Data
@EqualsAndHashCode(callSuper = false)
public class PendingPromotion extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @Column(name = "name_en", nullable = true, length = 100)
    @Size(max = 100)
    private String nameEn;

    @Column(name = "name_local", nullable = true, length = 100)
    @Size(max = 100)
    @NotEmpty
    @JsonProperty(value = "name_local")
    private String name;

    @Lob
    @Column(name = "description_en", nullable = true)
    private String descriptionEn;

    @Lob
    @Column(name = "description_local", nullable = true)
    @JsonProperty(value = "description_local")
    private String description;

    @Size(max = 140)
    @Column(name = "short_description_en", nullable = true)
    private String shortDescriptionEn;

    @Size(max = 140)
    @Column(name = "short_description_local", nullable = true)
    @JsonProperty(value = "short_description_local")
    private String shortDescription;

    @Column(name = "promotion_type", nullable = false, length = 50)
    @NotEmpty
    @Size(max = 50)
    private String type;

    @Column(name = "enable", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean enable;

    @Column(name = "member", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean member;

    @Column(name = "non_member", nullable = false, columnDefinition = "TINYINT(1)")
    @JsonProperty(value = "non_member")
    private Boolean nonMember;

    @Column(name = "promotion_repeat")
    private Integer repeat;

    @Transient
    @JsonProperty(value = "detail_data")
    private DetailData promotionData;

    @Lob
    @Column(name = "condition_data")
    @JsonIgnore
    private String conditionData;

    @Lob
    @Column(name = "detail_data")
    @JsonIgnore
    private String detailData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_period")
    @JsonProperty(value = "start_period")
    private Date startPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_period")
    @JsonProperty(value = "end_period")
    private Date endPeriod;

    @Column(name = "img_url")
    @JsonProperty(value = "img_web")
    private String imgUrl;

    @Column(name = "img_thm_url")
    @JsonProperty(value = "img_web_translation")
    private String imgThmUrl;

    @Column(name = "img_url_en")
    @JsonProperty(value = "img_mobile")
    private String imgUrlEn;

    @Column(name = "img_thm_url_en")
    @JsonProperty(value = "img_mobile_translation")
    private String imgThmUrlEn;

    @Transient
    @JsonProperty(value = "condition_data")
    private PromotionCondition promotionCondition;

    @Column(name = "app_ids")
    @JsonProperty(value = "app_ids")
    private String appId;

    @Column(name = "channel_type")
    @JsonProperty(value = "channel_type")
    private String channelType;

    @Column(name = "status")
    @JsonProperty(value = "status")
    private String status;

    @Column(name = "promotion_id", nullable = true)
    private Long promotionId;

    @Transient
    private Boolean live;

}