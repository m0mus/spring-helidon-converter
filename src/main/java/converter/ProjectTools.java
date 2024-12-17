package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import io.helidon.config.Config;

import dev.langchain4j.agent.tool.Tool;

public class ProjectTools {
    private final Path projectRoot;
    private final Path destinationRoot;
    private final List<String> inclusions;
    private final List<String> exclusions;

    public ProjectTools(Config config) {
        this.projectRoot = Path.of(config.get("project-root").as(String.class).orElse(""));
        this.destinationRoot = Path.of(config.get("destination-root").as(String.class).orElse(""));
        this.inclusions = config.get("inclusions").asList(String.class).orElse(Collections.emptyList());
        this.exclusions = config.get("exclusions").asList(String.class).orElse(Collections.emptyList());
    }

    @Tool("Returns a list of all project files. You can get content of each file using `getFile` or `getClass` methods.")
    public List<String> getProjectFiles() {
        System.out.println("Tool: getProjectFiles");
        var files = FileLister.listFiles(projectRoot.toString(), this.exclusions, this.inclusions);
        files.forEach(System.out::println);
        return files;
    }

    @Tool("Fetch the content of an original unconverted file.")
    public String readFile(String filePath) throws IOException {
        System.out.println("Tool: readFile, filePath: " + filePath);
        return Files.readString(projectRoot.resolve(filePath));
    }

    @Tool("Saves the converted file.")
    public void writeFile(String path, String content) throws IOException {
        System.out.println("Tool: writeFile, path: " + path);
        var destination = destinationRoot.resolve(path);
        Files.createDirectories(destination.getParent());

        if (content == null || content.isBlank()) {
            System.out.println("Creating empty file?!");
            Files.createFile(destination);
        } else {
            Files.writeString(destination, content);
        }
    }
}
