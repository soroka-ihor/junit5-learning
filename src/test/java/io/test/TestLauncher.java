package io.test;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

public class TestLauncher {

    public static void main(String[] args) {
        Launcher launcher = LauncherFactory.create();
        LauncherDiscoveryRequest discoveryRequest = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(DiscoverySelectors.selectPackage("io.test"))
                .build();

        SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
        launcher.execute(discoveryRequest, summaryGeneratingListener);

        try (PrintWriter printWriter = new PrintWriter(System.out)) {
            summaryGeneratingListener.getSummary().printTo(printWriter);
        }
    }

}
