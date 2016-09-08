package com.ascend.campaign.configs;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import com.ascend.campaign.Application;
import com.ascend.campaign.constants.CampaignEnum;
import com.wemall.aad.auth.filter.AuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.Filter;

@Configuration
@Slf4j
public class WebInitializerConfiguration extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    public FilterRegistrationBean authenticationFilter(Environment environment) {
        String activeProfile = environment.getActiveProfiles()[0];
        String authenEnvFileName = "aad.properties";
        if (CampaignEnum.ENV_STAGING.getContent().equalsIgnoreCase(activeProfile)
                || CampaignEnum.ENV_PRODUCTION.getContent().equalsIgnoreCase(activeProfile)) {
            authenEnvFileName = String.format("aad-%s.properties", activeProfile);
        }

        FilterRegistrationBean filter = new FilterRegistrationBean();
        filter.setFilter(new AuthenticationFilter());
        filter.addUrlPatterns("/api/*");
        filter.addInitParameter("envPath", getClass().getClassLoader().getResource(authenEnvFileName).getFile());
        filter.addInitParameter("exclusionPath",
                "/api/v1/itm/promotions/bundles, /api/v1/itm/promotions/mnp, "
                        + "/api/v1/itm/promotions/cart, /api/v1/deals/price, /api/v1/itm/promotions/freebie,"
                        + "/api/v1/itm/promotions/isFreebieCriteriaDuplicate, /api/v2/itm/promotions/cart,"
                        + "/api/v1/flashsales/wowExtra, /api/v1/flashsales/wowBanner,"
                        + "/api/v1/flashsales/products/status,"
                        + "/api/v1/flashsales/variants/status");
        filter.setName("authenticationFilter");

        return filter;
    }

    @Bean
    public Filter logbackMDCFilter() {
        MDCInsertingServletFilter mdcInsertingServletFilter = new MDCInsertingServletFilter();

        return mdcInsertingServletFilter;
    }
}
