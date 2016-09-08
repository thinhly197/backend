package com.ascend.campaign.utils;

import com.ascend.campaign.models.KieFactory;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;

public class KieUtil {

    public static KieFactory createKieFactory(String... drlResourcesPaths) {
        System.setProperty("drools.dateformat", "dd-MMM-yyyy HH:mm:ss");
        KieFactory kieFactory = new KieFactory();
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = createKieContainer(ks, drlResourcesPaths);

        // Configure and create the KieBase
        KieBaseConfiguration kieBaseConfiguration = ks.newKieBaseConfiguration();
        kieFactory.setKieBase(kieContainer.newKieBase(kieBaseConfiguration));

        // Configure and create the KieSession
        kieFactory.setKieBaseConfiguration(ks.newKieSessionConfiguration());
        return kieFactory;
    }

    public static StatelessKieSession createStatelessKieSession(KieFactory kieFactory) {
        return kieFactory.getKieBase()
                .newStatelessKieSession(kieFactory.getKieBaseConfiguration());
    }

    private static KieContainer createKieContainer(KieServices ks, String... drlResourcesPaths) {
        // Create the in-memory File System and add the resources files to it
        KieFileSystem kfs = ks.newKieFileSystem();
        for (String path : drlResourcesPaths) {
            kfs.write(ResourceFactory.newClassPathResource(path));
        }

        // Create the builder for the resources of the File System
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        // Build the Kie Bases
        kieBuilder.buildAll();

        // Check for errors
        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new IllegalArgumentException(kieBuilder.getResults().toString());
        }

        // Get the Release ID (mvn style: groupId, artifactId,version)
        ReleaseId relId = kieBuilder.getKieModule().getReleaseId();

        // Create the Container, wrapping the KieModule with the given ReleaseId
        return ks.newKieContainer(relId);
    }
}
