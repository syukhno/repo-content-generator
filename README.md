# GitHub Repository Content Concatenator

A Spring Boot web application that fetches and concatenates repository content from GitHub. This tool helps developers quickly view and aggregate content from multiple files in a GitHub repository, making it easier to analyze codebases and generate documentation.

![GitHub Repo Concatenator](./images/gh_concat.png)

## Features

- Web-based interface for entering GitHub repository URLs
- Configurable file pattern inclusion/exclusion
- Support for recursive directory traversal
- Real-time content generation with HTMX
- Copy-to-clipboard functionality
- Modern UI with Tailwind CSS

## Requirements

- Java 23 or higher
- Maven 3.6+
- GitHub Personal Access Token
- Modern web browser with JavaScript enabled

## Dependencies

Key dependencies include:

- Spring Boot 3.4.0
- JTE (Java Template Engine) 3.1.12
- HTMX 1.9.10
- Tailwind CSS
- Jackson (for JSON processing)

## Configuration

The application uses YAML configuration. Key settings:

```yaml
github:
   token: ${GITHUB_TOKEN}  # Set via environment variable
   includePatterns:
      - "**/*.md"
      - "**/*.txt"
      - "**/*.xml"
      - "**/*.java"
      - "**/*.jte"
      - "**/*.yaml"
      - "**/*.yml"
      - "**/*.graphqls"
   excludePatterns:
      - ".mvn/**"
      - ".idea/**"
      - "target/**"
      - ".gitignore"
      - ".gitattributes"
```

## Getting Started

1. Set up your GitHub token:
   ```bash
   export GITHUB_TOKEN=your_github_personal_access_token
   ```

2. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Access the web interface at `http://localhost:8080`

## How It Works

The application follows this process:

1. User submits a GitHub repository URL
2. Backend extracts owner and repository name
3. Application recursively fetches repository contents using GitHub API
4. Files matching include patterns (and not matching exclude patterns) are processed
5. Content is concatenated and returned to the frontend
6. Results are displayed in a copyable text area

## Code Example

Here's how to use the service programmatically:

```java
@Autowired
private GitHubService githubService;

public void processRepository(String owner, String repo) {
   try {
      githubService.downloadRepositoryContents(owner, repo);
      // Content will be saved to output/${repo}.md
   } catch (IOException e) {
      // Handle error
   }
}
```

## Development Notes

- JTE templates are used for server-side rendering
- Development mode is enabled by default for JTE
- For production, disable development mode and enable precompiled templates
- The application uses Spring Boot's RestClient for GitHub API communication

## Template Configuration

For production deployments, update `application.properties`:

```properties
# Development
gg.jte.development-mode=false
gg.jte.use-precompiled-templates=true
```

## Security Considerations

- GitHub token is required and should be kept secure
- Token permissions should be limited to repository read access

## Rate Limiting

The application is subject to GitHub API rate limits:

- Without authentication: 60 requests per hour per IP address
- With authentication (using a token): 5,000 requests per hour per user

If you exceed these limits, you'll receive a 403 Forbidden error like this:
```
Error generating readme: 403 Forbidden: "{"message":"API rate limit exceeded... Check out the documentation for more details."}"
```

To avoid rate limiting issues:
- Always use an authenticated token
- Monitor your API usage through GitHub's API
- Consider implementing request caching for frequently accessed repositories
- For large repositories, plan your requests carefully to stay within limits

You can check your current rate limit status by calling:
`https://api.github.com/rate_limit`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to your branch
5. Create a Pull Request

## License

[Add your license information here]

## Support

For issues and feature requests, please use the GitHub issue tracker.