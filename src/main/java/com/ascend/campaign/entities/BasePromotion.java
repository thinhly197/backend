package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public class BasePromotion extends BaseEntity {
    @Column(name = "name_en", nullable = true, length = 100)
    @Size(max = 100)
    private String nameEn;

    @Column(name = "name_local", nullable = true, length = 100)
    @Size(min = 1, max = 100)
    @NotEmpty
    @JsonProperty(value = "name_local")
    @NotNull
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
    @NotNull
    private String shortDescription;

    @Column(name = "enable", nullable = false, columnDefinition = "TINYINT(1)")
    @NotNull
    private Boolean enable;

    @NotNull
    @Column(name = "member", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean member;

    @NotNull
    @Column(name = "non_member", nullable = false, columnDefinition = "TINYINT(1)")
    @JsonProperty(value = "non_member")
    private Boolean nonMember;

    @Column(name = "promotion_repeat")
    private Integer repeat;

    @Lob
    @Column(name = "condition_data")
    @JsonIgnore
    private String conditionData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_period")
    @JsonProperty(value = "start_period")
    @NotNull
    private Date startPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_period")
    @JsonProperty(value = "end_period")
    @NotNull
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


    @Column(name = "app_ids")
    @JsonProperty(value = "app_ids")
    private String appId;

    @Column(name = "channel_type")
    @JsonProperty(value = "channel_type")
    private String channelType;

    @PrePersist
    void preInsertPromotion() {
        setDefaultBooleanField();
        patchStartPeriodWithEndOfDay();
        patchEndPeriodWithEndOfDay();
    }

    @PreUpdate
    void preUpdatePromotion() {
        setDefaultBooleanField();
        patchStartPeriodWithEndOfDay();
        patchEndPeriodWithEndOfDay();
    }

    void setDefaultBooleanField() {
        if (this.member == null) {
            this.member = false;
        }

        if (this.nonMember == null) {
            this.nonMember = false;
        }

        if (this.enable == null) {
            this.enable = false;
        }
    }

    void patchStartPeriodWithEndOfDay() {
        this.startPeriod = patchDateProtectEndOfDay(startPeriod);
    }

    void patchEndPeriodWithEndOfDay() {
        this.endPeriod = patchDateProtectEndOfDay(endPeriod);
    }

    Date patchDateProtectEndOfDay(Date date) {
        if (date != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            DateTime dt = new DateTime(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND));

            return dt.toDate();
        }

        return null;
    }
}
