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
@Table(name = "version_migrations")
@Data
@EqualsAndHashCode(callSuper = false)
public class VersionMigration extends BaseEntity {

    @Column(name = "version_migrations", length = 100)
    @Size(max = 100)
    private String versionMigrations;

    @Column(name = "migrations_note", length = 200)
    @Size(max = 200)
    private String migrationsNote;

}