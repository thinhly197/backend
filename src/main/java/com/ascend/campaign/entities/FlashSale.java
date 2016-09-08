package com.ascend.campaign.entities;

import com.ascend.campaign.constants.FlashSaleEnum;
import com.ascend.campaign.constants.FlashSaleTypeEnum;
import com.ascend.campaign.models.BannerImages;
import com.ascend.campaign.models.FlashSaleCondition;
import com.ascend.campaign.validators.FlashSaleConditionCheck;
import com.ascend.campaign.validators.VerifyValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = " flash_sale")
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class FlashSale extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    @Size(max = 100)
    @NotEmpty
    @NotNull
    @JsonProperty(value = "name")
    private String name;

    @Column(name = "name_translation", nullable = true, length = 100)
    @Size(max = 100)
    private String nameTranslation;

    @Size(max = 140)
    @Column(name = "short_description", nullable = true)
    @JsonProperty(value = "short_description")
    private String shortDescription;

    @Size(max = 140)
    @Column(name = "short_description_translation", nullable = true)
    private String shortDescriptionTranslation;

    @Column(name = "type", nullable = false, length = 50, updatable = false)
    @NotEmpty
    @Size(max = 50)
    @VerifyValue(FlashSaleTypeEnum.class)
    private String type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_period")
    @JsonProperty(value = "start_period")
    @NotNull
    private Date startPeriod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_period")
    @NotNull
    @JsonProperty(value = "end_period")
    private Date endPeriod;

    @Column(name = "enable", columnDefinition = "TINYINT(1)")
    @NotNull
    private Boolean enable;

    @Column(name = "member", nullable = false, columnDefinition = "TINYINT(1)")
    @NotNull
    private Boolean member;

    @Column(name = "non_member", nullable = false, columnDefinition = "TINYINT(1)")
    @JsonProperty(value = "non_member")
    @NotNull
    private Boolean nonMember;

    @ManyToOne
    @JoinColumn(name = "app_id", nullable = false)
    @NotNull
    private AppId appId;

    @JsonProperty(value = "banner_images")
    @Transient
    private BannerImages bannerImages;

    @Lob
    @Column(name = "banner_images")
    @JsonIgnore
    private String bannerImagesData;

    @Transient
    @JsonProperty(value = "flashsale_condition")
    @FlashSaleConditionCheck(FlashSaleEnum.FLASH_SALE)
    private FlashSaleCondition flashSaleCondition;

    @Lob
    @Column(name = "condition_data", nullable = false)
    @JsonIgnore
    private String conditionData;

    @Size(max = 50)
    @Column(name = "partner", nullable = true)
    @JsonProperty(value = "partner")
    private String partner;

    @Transient
    private Boolean live;

    @Transient
    @JsonIgnore
    private double minVariantPrice;

    @Transient
    @JsonProperty(value = "filter_status")
    private String filterStatus;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flashSale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FlashSaleProduct> flashSaleProductList;


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
