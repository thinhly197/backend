package com.ascend.campaign.configs;

import com.ascend.campaign.constants.CampaignEnum;
import com.ascend.campaign.models.AuthenticationModel;
import com.ascend.campaign.services.ConfigurationService;
import com.nimbusds.jose.JOSEException;
import com.wemall.aad.auth.helper.TokenHelper;
import com.wemall.aad.auth.jwt.exception.JWTVerifyException;
import lombok.NonNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class MDCFilter implements Filter {

    @NonNull
    private final ConfigurationService configurationService;

    @Autowired
    public MDCFilter(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void destroy() {
    }

    private String getEnv() {
        String activeProfile = configurationService.getProfiles();
        if (CampaignEnum.ENV_PRODUCTION.getContent().equalsIgnoreCase(activeProfile)) {
            return "prod";
        }
        return activeProfile;
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) request;
        String correlationId = rq.getHeader("x-wm-correlationId");
        String accessToken = rq.getHeader("x-wm-accessToken");

        AuthenticationModel authModel = new AuthenticationModel();

        if (correlationId != null && correlationId.isEmpty()) {
            correlationId = "";
        }
        if (accessToken != null && !accessToken.isEmpty()) {
            TokenHelper tokenHelper = new TokenHelper(getEnv());
            try {
                Optional<Map<String, Object>> profile = Optional.ofNullable(tokenHelper.getPayloadInfo(accessToken));
                if (profile.isPresent()) {
                    authModel.setTyp(profile.get().get("typ") == null ? "" : profile.get().get("typ").toString());
                    authModel.setUsr(profile.get().get("usr") == null ? "" : profile.get().get("usr").toString());
                    authModel.setExp(
                            new Long(profile.get().get("exp") == null ? "" : profile.get().get("exp").toString()));
                }
            } catch (JOSEException e) {
                e.printStackTrace();
            } catch (JWTVerifyException e) {
                e.printStackTrace();
            }
        } else {
            authModel.setUsr("");
        }

        MDC.put(CampaignEnum.LOG_TYPE.getContent(), "APP");
        MDC.put(CampaignEnum.COMPONENT.getContent(), "CMP");
        MDC.put(CampaignEnum.CORRELATION.getContent(), correlationId);
        MDC.put(CampaignEnum.ANONYMOUS.getContent(), Optional.ofNullable(rq.getHeader("x-wm-anonymousId")).orElse(""));
        MDC.put(CampaignEnum.USER_KEY.getContent(), authModel.getUsr());

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    public void init(FilterConfig arg0) throws ServletException {
    }
}