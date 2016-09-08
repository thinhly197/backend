package com.ascend.campaign.entities;

import com.ascend.campaign.constants.CampaignEnum;
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

@Entity
@Table(name = "campaign")
@Data
@EqualsAndHashCode(callSuper = false)
public class Campaign extends BaseEntity {
    @Column(name = "name_translation", nullable = true, length = 100)
    @Size(max = 100)
    private String nameTranslation;

    @Column(name = "name", nullable = true, length = 100)
    @Size(max = 100)
    @NotEmpty
    @JsonProperty(value = "name")
    private String name;

    @Lob
    @Column(name = "detail_translation", nullable = true)
    @JsonProperty(value = "detail_translation")
    private String detailTranslation;

    @Lob
    @Column(name = "detail", nullable = true)
    @JsonProperty(value = "detail")
    private String detail;

    @Column(name = "enable", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean enable;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_period")
    @JsonProperty(value = "start_period")
    private Date startPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_period")
    @JsonProperty(value = "end_period")
    private Date endPeriod;

    @Transient
    private String businessChannel = CampaignEnum.ITRUEMART.getContent();

    @Transient
    private Boolean live;

    @Transient
    @JsonProperty(value = "filter_status")
    private String filterStatus;

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