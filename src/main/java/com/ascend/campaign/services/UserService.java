package com.ascend.campaign.services;

import com.ascend.campaign.entities.User;
import com.ascend.campaign.repositories.UserRepo;
import com.ascend.campaign.utils.JSONUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
public class UserService {
    @NonNull
    private final UserRepo userRepo;

    @Autowired
    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User updateCustomerId(Long userId, String customerId) {
        log.warn("content={\"activity\":\"Update User By Customer\", \"msg\":{\"user_id\":\"{}\", "
                + "\"customer_id\":\"{}\"}}", userId, customerId);
        User user = userRepo.getOne(userId);
        user.setCustomerId(customerId);

        return userRepo.saveAndFlush(user);
    }

    public User createUser(User entity) {
        log.warn("content={\"activity\":\"Create User\", \"msg\":{}}", JSONUtil.toString(entity));
        return userRepo.saveAndFlush(entity);
    }

    public List<User> listUsers() {
        return userRepo.findAll();
    }

    public User getUserByCustomerId(String customerId) {
        return userRepo.findByCustomerId(customerId);
    }
}
