package dev.danvega.cg.local;

import dev.danvega.cg.gh.GitHubConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Service
public class LocalFileService {
    private static final Logger log = LoggerFactory.getLogger(LocalFileService.class);
    private final GitHubConfiguration config;

    public LocalFileService(GitHubConfiguration config) {
        this.config = config;
    }

    public void processLocalDirectory(String directoryPath, String outputFileName) throws IOException {
        Path sourceDir = Paths.get(directoryPath);
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        StringBuilder contentBuilder = new StringBuilder();
        processDirectoryRecursively(sourceDir, contentBuilder);

        Path outputDir = Paths.get("output");
        Files.createDirectories(outputDir);
        Path outputFile = outputDir.resolve(outputFileName + ".md");
        Files.write(outputFile, contentBuilder.toString().getBytes());

        log.info("Local directory contents written to: {}", outputFile.toAbsolutePath());
    }

    private void processDirectoryRecursively(Path directory, StringBuilder contentBuilder) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .filter(this::shouldIncludeFile)
                    .forEach(file -> {
                        try {
                            String relativePath = directory.relativize(file).toString();
                            String content = Files.readString(file);
                            contentBuilder.append("File: ").append(relativePath).append("\n\n");
                            contentBuilder.append(content).append("\n\n");
                        } catch (IOException e) {
                            log.error("Error reading file: {}", file, e);
                        }
                    });
        }
    }

    private boolean shouldIncludeFile(Path filePath) {
        String path = filePath.toString();

        if (matchesPatterns(path, config.excludePatterns())) {
            log.debug("File {} excluded by exclude patterns", path);
            return false;
        }

        return matchesPatterns(path, config.includePatterns());
    }

    private boolean matchesPatterns(String path, List<String> patterns) {
        if (patterns.isEmpty()) {
            return patterns == config.excludePatterns();
        }

        return patterns.stream()
                .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + pattern.trim()))
                .anyMatch(matcher -> matcher.matches(Paths.get(path)));
    }
}
