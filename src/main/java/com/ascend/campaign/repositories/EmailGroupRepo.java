package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.EmailGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmailGroupRepo extends JpaRepository<EmailGroup, Long>, JpaSpecificationExecutor<EmailGroup> {
}
