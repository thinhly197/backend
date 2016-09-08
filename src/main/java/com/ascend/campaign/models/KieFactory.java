package com.ascend.campaign.models;

import lombok.Data;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSessionConfiguration;

@Data
public class KieFactory {
    private KieBase kieBase;
    private KieSessionConfiguration kieBaseConfiguration;
}
