package com.ascend.campaign.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {
    @Column(name = "email", unique = true, length = 100)
    @Email
    @Size(max = 100)
    private String email;

    @Column(name = "first_name", length = 100)
    @JsonProperty(value = "first_name")
    @Size(max = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    @JsonProperty(value = "last_name")
    @Size(max = 100)
    private String lastName;

    @Column(name = "customer_id", unique = true, length = 100)
    @JsonProperty(value = "customer_id")
    @Size(max = 100)
    private String customerId;
}