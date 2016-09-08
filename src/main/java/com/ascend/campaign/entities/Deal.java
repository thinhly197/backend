package com.ascend.campaign.entities;

import com.ascend.campaign.models.DealCondition;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "deal")
@Data
@EqualsAndHashCode(callSuper = false)
public class Deal extends BaseEntity {
    @Column(name = "channel_type")
    @JsonProperty(value = "channel_type")
    Integer channelType;

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

    @Column(name = "type", nullable = false, length = 50)
    @NotEmpty
    @Size(max = 50)
    private String type;

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

    @Column(name = "enable", columnDefinition = "TINYINT(1)")
    private Boolean enable;

    @Column(name = "member", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean member;

    @Column(name = "non_member", nullable = false, columnDefinition = "TINYINT(1)")
    @JsonProperty(value = "non_member")
    private Boolean nonMember;


    @Transient
    @Column(name = "condition_data")
    @JsonProperty(value = "condition_data")
    private DealCondition dealCondition;

    @Lob
    @Column(name = "condition_data", nullable = false)
    @JsonIgnore
    private String conditionData;

    @Column(name = "is_super_deal", columnDefinition = "TINYINT(1)")
    @JsonProperty(value = "is_super_deal")
    private Boolean superDeal;

    @Transient
    @JsonProperty(value = "payment_types")
    private List<String> paymentType;


    @Transient
    private Boolean live;

    @PrePersist
    void preInsertPromotion() {
        patchStartPeriodWithEndOfDay();
        patchEndPeriodWithEndOfDay();
    }

    @PreUpdate
    void preUpdatePromotion() {
        patchStartPeriodWithEndOfDay();
        patchEndPeriodWithEndOfDay();
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
