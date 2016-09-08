package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

@Entity
@Table(name = "email", uniqueConstraints = @UniqueConstraint(columnNames = {"emailgroup_id", "email"}))
@Data
@EqualsAndHashCode(callSuper = false)
public class Email extends BaseEntity {
    @Column(name = "emailgroup_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @JsonProperty(value = "emailgroup_id")
    private Long emailGroupId;

    @Column(name = "email", unique = false, length = 100)
    @org.hibernate.validator.constraints.Email
    @Size(max = 100)
    private String email;
}
