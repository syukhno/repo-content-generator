package dev.danvega.cg;

import dev.danvega.cg.gh.GitHubService;
import dev.danvega.cg.local.LocalFileService;
import gg.jte.TemplateEngine;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public GitHubService gitHubService() {
        return mock(GitHubService.class);
    }

    @Bean
    @Primary
    public LocalFileService localFileService() {
        return mock(LocalFileService.class);
    }

    @Bean
    @Primary
    public TemplateEngine templateEngine() {
        return mock(TemplateEngine.class);
    }

    @Bean
    @Primary
    public ContentGeneratorService contentGeneratorService() {
        return mock(ContentGeneratorService.class);
    }
}
