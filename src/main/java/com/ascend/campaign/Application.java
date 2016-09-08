package com.ascend.campaign;

import com.ascend.campaign.services.DroolsService;
import com.ascend.campaign.services.MigrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
@Slf4j
public class Application {
    @Autowired
    DroolsService droolsService;

    @Autowired
    MigrationService migrationService;


    public static void main(String... args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        try {
            log.warn("Memory: Max[{}], Free[{}], Total[{}]", Runtime.getRuntime().maxMemory(),
                    Runtime.getRuntime().freeMemory(), Runtime.getRuntime().totalMemory());
            StopWatch stopWatch = new StopWatch("Application");

            stopWatch.start("build itruemart rule");
            droolsService.buildDrlPromotionWhenApplicationStart();
            stopWatch.stop();

            log.info(stopWatch.prettyPrint());
        } catch (Exception ex) {
            log.error("Exception occur while build rule !!", ex);
        }
    }
}
