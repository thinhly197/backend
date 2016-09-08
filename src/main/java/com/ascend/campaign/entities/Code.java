package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "code")
@Data
@EqualsAndHashCode(callSuper = false)
public class Code extends BaseEntity {
    @Column(name = "code", unique = true, length = 15)
    @JsonProperty(value = "code")
    @Size(max = 15)
    private String code;

    @Column(name = "revenue")
    @JsonProperty(value = "revenue")
    private Double revenue;

    @Column(name = "status", length = 10)
    @JsonProperty(value = "status")
    @Size(max = 10)
    private String status;

    @Column(name = "used")
    @JsonProperty(value = "used")
    private Long use;

    // limit of uses
    @Column(name = "total")
    @JsonProperty(value = "total")
    private Long available;

    // limit of times/users
    @Column(name = "time_per_user")
    @JsonProperty(value = "limit_of_time_or_user")
    private Integer limitOfTimeOrUser;

    @Column(name = "detail_id")
    @JsonIgnore
    private Long codeDetail;

}
