package com.ascend.campaign.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@Slf4j
public class StaticController {

    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public String index() {
        Optional<String> catalinaHome = Optional.ofNullable(System.getenv("CATALINA_HOME"));
        Optional<String> catalinaProp = Optional.ofNullable(System.getProperty("catalina.home"));
        Optional<String> catalinaBase = Optional.ofNullable(System.getProperty("catalina.base"));
        String versionContent = "version.txt or CATALINA_HOME is not set";
        try {
            String catalinaPath = catalinaHome.orElse(catalinaBase.orElse(catalinaProp.orElse("")));
            versionContent = new String(Files.readAllBytes(Paths.get(catalinaPath + "/webapps/version.txt")));
        } catch (Exception e) {
            log.error(" content={\"activity\":\"Vertion\", \"msg\":\"" + "Exception occurs: {}", e.getMessage(), e
                    + "\"}");
        }

        return versionContent;
    }
}