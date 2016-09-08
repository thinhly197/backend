package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepo extends JpaRepository<Email, Long> {
    List<Email> findByEmailGroupId(Long emailGroupId);

    Long countByEmailGroupId(Long code);

    void deleteByEmailGroupId(long emailGroupId);
}
