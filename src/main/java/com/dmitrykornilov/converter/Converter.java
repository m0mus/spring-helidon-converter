package com.dmitrykornilov.converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import io.helidon.config.Config;
import io.helidon.config.FileConfigSource;

public class Converter {
    private final Config config;
    private final Path projectRoot;
    private final Path destinationRoot;
    private final ServiceFactory serviceFactory;

    public Converter(Path configPath) {
        this.config = Config.create(FileConfigSource.builder()
                                            .path(configPath.resolve("converter.properties"))
                                            .build());

        // Initialize the fields related to configuration
        this.projectRoot = Path.of(config.get("project-root").asString().orElse(""));
        this.destinationRoot = Path.of(config.get("destination-root").asString().orElse(""));

        this.serviceFactory = new ServiceFactory(configPath, config);
    }

    public void run() throws IOException {
        // Create services
        var converterService = serviceFactory.createConverterService();
        var fileTypeService = serviceFactory.createFileTypeService();

        // Read inclusions and exclusions from configuration
        var inclusions = config.get("inclusions").asList(String.class).orElse(Collections.emptyList());
        var exclusions = config.get("exclusions").asList(String.class).orElse(Collections.emptyList());

        // Process files
        var files = FileLister.listFiles(projectRoot.toString(), exclusions, inclusions);
        for (var file : files) {
            var fileContent = Files.readString(file);
            var fileType = fileTypeService.identifyFileType(file.getFileName().toString(), fileContent);
            System.out.println("File: " + file + ", Type: " + fileType);

            if (!fileType.equals("UNIDENTIFIED")) {
                var converted = converterService.convert(fileType, fileContent);
                writeFile(file, converted);
            }
        }
    }

    private void writeFile(Path path, String content) throws IOException {
        var relativePath = projectRoot.relativize(path);
        var destination = destinationRoot.resolve(relativePath);
        Files.createDirectories(destination.getParent());

        if (content == null || content.isBlank()) {
            Files.createFile(destination);
        } else {
            Files.writeString(destination, content);
        }
    }

    public static void main(String[] args) {
        try {
            var configPath = args.length > 0 ? Path.of(args[0]) : Path.of("");
            var app = new Converter(configPath);
            app.run();
        } catch (Exception e) {
            System.err.println("Application failed: " + e.getMessage());
        }
    }
}
