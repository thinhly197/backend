package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

@Entity
@Table(name = "email_group")
@Data
@EqualsAndHashCode(callSuper = false)
public class EmailGroup extends BaseEntity {
    @Column(name = "name", length = 100)
    @JsonProperty(value = "name")
    @Size(max = 100)
    private String name;

    @Column(name = "description", length = 2000)
    @JsonProperty(value = "description")
    @Size(max = 2000)
    private String description;

    @Transient
    @JsonProperty(value = "quantity")
    private Long quantity;

}
