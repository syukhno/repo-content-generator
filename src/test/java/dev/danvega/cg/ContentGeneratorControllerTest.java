package dev.danvega.cg;

import dev.danvega.cg.gh.GitHubService;
import dev.danvega.cg.local.LocalFileService;
import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContentGeneratorController.class)
@Import({
        TestConfig.class
})
class ContentGeneratorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GitHubService gitHubService;

    @Autowired
    private LocalFileService localFileService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ContentGeneratorService contentGeneratorService;

    @BeforeEach
    void setUp() {
        reset(gitHubService, localFileService, templateEngine, contentGeneratorService);
    }

    @Test
    void index_ShouldReturnIndexView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void generate_WithValidGitHubUrl_ShouldReturnContent() throws Exception {
        // Arrange
        String githubUrl = "https://github.com/user/repo";
        String generatedContent = "Generated content";
        String renderedTemplate = "Rendered template";

        when(contentGeneratorService.generateContent(eq(githubUrl), eq(null)))
                .thenReturn(generatedContent);
        doAnswer(invocation -> {
            StringOutput output = invocation.getArgument(2);
            output.writeContent(renderedTemplate);
            return null;
        }).when(templateEngine).render(eq("result.jte"), any(), any(StringOutput.class));

        // Act & Assert
        mockMvc.perform(post("/generate")
                        .param("githubUrl", githubUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(renderedTemplate));

        verify(contentGeneratorService).generateContent(githubUrl, null);
        verify(templateEngine).render(eq("result.jte"), any(), any(StringOutput.class));
    }

    @Test
    void generate_WithValidLocalPath_ShouldReturnContent() throws Exception {
        // Arrange
        String localPath = "/path/to/local/file";
        String generatedContent = "Generated content";
        String renderedTemplate = "Rendered template";

        when(contentGeneratorService.generateContent(eq(null), eq(localPath)))
                .thenReturn(generatedContent);
        doAnswer(invocation -> {
            StringOutput output = invocation.getArgument(2);
            output.writeContent(renderedTemplate);
            return null;
        }).when(templateEngine).render(eq("result.jte"), any(), any(StringOutput.class));

        // Act & Assert
        mockMvc.perform(post("/generate")
                        .param("localPath", localPath)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string(renderedTemplate));

        verify(contentGeneratorService).generateContent(null, localPath);
        verify(templateEngine).render(eq("result.jte"), any(), any(StringOutput.class));
    }

    @Test
    void generate_WithNoInputs_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/generate")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Either GitHub URL or local path must be provided."));

        verifyNoInteractions(contentGeneratorService, templateEngine);
    }

    @Test
    void generate_WithBlankInputs_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/generate")
                        .param("githubUrl", "")
                        .param("localPath", "  ")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error: Either GitHub URL or local path must be provided."));

        verifyNoInteractions(contentGeneratorService, templateEngine);
    }

    @Test
    void generate_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Arrange
        String githubUrl = "https://github.com/user/repo";
        String errorMessage = "Service error";
        when(contentGeneratorService.generateContent(eq(githubUrl), eq(null)))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        mockMvc.perform(post("/generate")
                        .param("githubUrl", githubUrl)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error generating content: " + errorMessage));

        verify(contentGeneratorService).generateContent(githubUrl, null);
        verifyNoInteractions(templateEngine);
    }
}