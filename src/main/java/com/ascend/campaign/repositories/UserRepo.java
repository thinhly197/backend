package com.ascend.campaign.repositories;

import com.ascend.campaign.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByCustomerId(String customerId);
}
