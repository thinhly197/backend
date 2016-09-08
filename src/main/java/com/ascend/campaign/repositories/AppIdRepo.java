package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.AppId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppIdRepo extends JpaRepository<AppId, Long> {
    AppId findByName(String name);
}
