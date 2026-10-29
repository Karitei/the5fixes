package com.the5fixes.audit;

import com.the5fixes.audit.Type.LighthouseRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
public class LighthouseRunnerTest {

    @Autowired
    LighthouseRunner runner;

    @Test
    @Disabled("Nécessite le réseau et environ 60 secondes — à lancer à la main")
    void shouldAuditARealSite(){
        LighthouseRecord result = runner.run("https://www.mozilla.org");


        System.out.println("Performance      : " + result.getPerformance());
        System.out.println("Accessibilité    : " + result.getAccessibility());
        System.out.println("Bonnes pratiques : " + result.getBestPractices());
        System.out.println("SEO              : " + result.getSeo());
        System.out.println("LCP              : " + result.getLoadTimeSeconds() + " s");
        System.out.println("Poids            : " + result.getPageSizeBytes() + " octets");

        assertThat(result.getPerformance()).isGreaterThan(0);
        assertThat(result.getSeo()).isGreaterThan(0);
        assertThat(result.getLoadTimeSeconds()).isGreaterThan(0);
    }
}
