package dev.danvega.gh;

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

/**
 * Controller class for handling GitHub-related operations in the readme generator application.
 * This controller manages the main page rendering and readme generation process.
 */
@Controller
public class GitHubController {

    private static final Logger log = LoggerFactory.getLogger(GitHubController.class);
    private final GitHubService ghService;
    private final TemplateEngine templateEngine;

    /**
     * Constructs a new GitHubController with the specified GitHubService and TemplateEngine.
     *
     * @param ghService      The GitHubService used for interacting with GitHub repositories.
     * @param templateEngine The TemplateEngine used for rendering JTE templates.
     */
    public GitHubController(GitHubService ghService, TemplateEngine templateEngine) {
        this.ghService = ghService;
        this.templateEngine = templateEngine;
    }

    /**
     * Handles GET requests to the root URL ("/") and renders the index page.
     *
     * @return The name of the index template to be rendered.
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * Handles POST requests to "/generate" for generating readme content from a GitHub repository.
     * This method downloads the repository contents, processes them, and returns the result as HTML.
     *
     * @param githubUrl The URL of the GitHub repository to generate the readme from.
     * @return A string containing the HTML representation of the generated readme content.
     *         If an error occurs, it returns an error message instead.
     */
    @PostMapping("/generate")
    @ResponseBody
    public String generateReadme(@RequestParam String githubUrl) {
        try {
            // Extract owner and repo from GitHub URL
            String[] parts = githubUrl.split("/");
            String owner = parts[parts.length - 2];
            String repo = parts[parts.length - 1];

            // Download repository contents and read the generated file
            ghService.downloadRepositoryContents(owner, repo);
            String content = new String(Files.readAllBytes(Paths.get("output", repo + ".md")));
            StringOutput output = new StringOutput();
            templateEngine.render("result.jte", Map.of("content", content), output);
            return output.toString();
        } catch (Exception e) {
            log.error("Error generating readme", e);
            return "Error generating readme: " + e.getMessage();
        }
    }
}

