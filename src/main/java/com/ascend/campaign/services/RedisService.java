package com.ascend.campaign.services;

import com.ascend.campaign.constants.CampaignEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Slf4j
public class RedisService {

    @NonNull
    private StringRedisTemplate redisTemplate;

    @NonNull
    private Environment environment;

    @Autowired
    public RedisService(StringRedisTemplate redisTemplate, Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
    }

    public String syncRedisDrlFileName(String key, String defaultDrlFileName, String profile, boolean isUpdated) {
        if (!CampaignEnum.ENV_LOCAL.getContent().equalsIgnoreCase(profile)
                && !CampaignEnum.ENV_TEST.getContent().equalsIgnoreCase(profile)) {
            StopWatch stopWatch = new StopWatch("Sync drools file on redis");
            ValueOperations<String, String> ops = this.redisTemplate.opsForValue();

            if (isUpdated) {
                stopWatch.start("redis set new value");
                ops.set(key, defaultDrlFileName);
                stopWatch.stop();
            } else {
                stopWatch.start("redis get");
                defaultDrlFileName = ops.get(key);
                stopWatch.stop();
            }

            log.info(stopWatch.prettyPrint());
        }

        return defaultDrlFileName;
    }

    public boolean isFileNotExists(String filepath) {
        Optional<URL> urlFile = Optional.ofNullable(getClass().getResource(filepath));
        if (urlFile.isPresent()) {
            return Files.notExists(Paths.get(urlFile.get().getFile()), LinkOption.NOFOLLOW_LINKS);
        } else {
            return true;
        }
    }

    public String getBuildDrools(String key) {
        String activeProfile = environment.getActiveProfiles()[0];
        if (CampaignEnum.ENV_LOCAL.getContent().equalsIgnoreCase(activeProfile)
                || CampaignEnum.ENV_TEST.getContent().equalsIgnoreCase(activeProfile)) {
            return CampaignEnum.NO.getContent();
        }
        ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
        return ops.get(key);
    }

    public void setBuildDrools(String key, String isBuild) {
        String activeProfile = environment.getActiveProfiles()[0];

        if (!CampaignEnum.ENV_LOCAL.getContent().equalsIgnoreCase(activeProfile)
                && !CampaignEnum.ENV_TEST.getContent().equalsIgnoreCase(activeProfile)) {
            ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
            ops.set(key, isBuild);
        }
    }
}
