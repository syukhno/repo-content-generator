package dev.danvega.cg.gh;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@ConfigurationProperties(value = "github")
public record GitHubConfiguration(String token, List<String> includePatterns, @DefaultValue("") List<String> excludePatterns) {

    public GitHubConfiguration {
        if (includePatterns == null) {
            throw new IllegalArgumentException("includePatterns must not be null");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("GitHub token must not be null or blank");
        }
        // Ensure excludePatterns is never null
        if (excludePatterns == null) {
            excludePatterns = List.of();
        }
    }
}
