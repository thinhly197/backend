package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.VersionMigration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VersionMigrationRepo extends JpaRepository<VersionMigration, Long>,
        JpaSpecificationExecutor<VersionMigration> {

    VersionMigration findByVersionMigrations(String content);

    void deleteByVersionMigrations(String versionMigrations);
}