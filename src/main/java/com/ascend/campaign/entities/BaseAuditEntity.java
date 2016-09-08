package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import java.util.Date;

@MappedSuperclass
@Data
public class BaseAuditEntity {
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "created_at")
    private Date createdAt;

    @Size(max = 100)
    @Column(name = "created_by", length = 100)
    @JsonProperty(value = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty(value = "updated_at")
    private Date updatedAt;

    @Size(max = 100)
    @Column(name = "updated_by", length = 100)
    @JsonProperty(value = "updated_by")
    private String updatedBy;

    @PrePersist
    public void preInsert() {
        if (this.createdAt == null && this.updatedAt == null) {
            this.createdAt = this.updatedAt = DateTime.now().toDate();
        }

    }

    @PreUpdate
    public void preUpdate() {
        if (this.updatedAt == null) {
            this.updatedAt = DateTime.now().toDate();
        }
    }
}
