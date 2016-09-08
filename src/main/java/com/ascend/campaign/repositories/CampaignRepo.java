package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CampaignRepo extends JpaRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {
    List<Campaign> findByName(String campaignName);
}
