package dev.danvega.cg;

import dev.danvega.cg.gh.GitHubConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportRuntimeHints;

@ImportRuntimeHints(ResourceBundleRuntimeHints.class)
@EnableConfigurationProperties(GitHubConfiguration.class)
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
