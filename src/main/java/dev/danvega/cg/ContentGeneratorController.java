package dev.danvega.cg;

import dev.danvega.cg.gh.GitHubService;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class ContentGeneratorController {
    private static final Logger log = LoggerFactory.getLogger(ContentGeneratorController.class);
    private final GitHubService ghService;
    private final LocalFileService localFileService;
    private final TemplateEngine templateEngine;

    public ContentGeneratorController(GitHubService ghService, LocalFileService localFileService, TemplateEngine templateEngine) {
        this.ghService = ghService;
        this.localFileService = localFileService;
        this.templateEngine = templateEngine;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/generate")
    @ResponseBody
    public String generateReadme(@RequestParam(required = false) String githubUrl,
                                 @RequestParam(required = false) String localPath) {
        try {
            String content;
            if (githubUrl != null && !githubUrl.isBlank()) {
                String[] parts = githubUrl.split("/");
                String owner = parts[parts.length - 2];
                String repo = parts[parts.length - 1];
                ghService.downloadRepositoryContents(owner, repo);
                content = new String(Files.readAllBytes(Paths.get("output", repo + ".md")));
            } else if (localPath != null && !localPath.isBlank()) {
                String outputName = Paths.get(localPath).getFileName().toString();
                localFileService.processLocalDirectory(localPath, outputName);
                content = new String(Files.readAllBytes(Paths.get("output", outputName + ".md")));
            } else {
                throw new IllegalArgumentException("Either GitHub URL or local path must be provided");
            }

            StringOutput output = new StringOutput();
            templateEngine.render("result.jte", Map.of("content", content), output);
            return output.toString();
        } catch (Exception e) {
            log.error("Error generating content", e);
            return "Error generating content: " + e.getMessage();
        }
    }
}

