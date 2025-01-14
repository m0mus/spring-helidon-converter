package net.dmitrykornilov.converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.helidon.config.Config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class ServiceFactory {
    private final Path configPath;
    private final Config config;
    private final ChatLanguageModel model;

    private final Map<String, String> promptsCashed = new HashMap<>();

    public ServiceFactory(Path configPath, Config config) {
        this.configPath = configPath;
        this.config = config;
        this.model = createModel();
    }

    public FileTypeService createFileTypeService() {
        return AiServices.builder(FileTypeService.class)
                .chatLanguageModel(model)
                .systemMessageProvider(memoryId -> readFileTypePrompt())
                .build();
    }

    public ConverterService createConverterService() {
        return AiServices.builder(ConverterService.class)
                .chatLanguageModel(model)
                .systemMessageProvider(fileType -> readConvertPrompt((String)fileType))
                .build();
    }

    private String readFileTypePrompt() {
        return readPrompt("prompt_identify-file-type.txt");
    }

    private String readConvertPrompt(String fileType) {
        var promptFile = "prompt_" + fileType.toLowerCase().replace("_", "-") + ".txt";
        return readPrompt(promptFile);
    }

    private String readPrompt(String promptFile) {
        if (promptsCashed.containsKey(promptFile)) {
            return promptsCashed.get(promptFile);
        }

        try {
            var path = this.configPath.resolve(promptFile);
            var content = Files.readString(path);
            promptsCashed.put(promptFile, content);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ChatLanguageModel createModel() {
        var modelName = config.get("model.name").asString().orElse(GPT_4_O_MINI.toString());
        System.out.println("Model: " + modelName);
        return OpenAiChatModel.builder()
                .apiKey(config.get("model.api-key").asString().orElse("demo"))
                .baseUrl(config.get("model.base-url").asString().orElse(null))
                .modelName(modelName)
                .timeout(Duration.ofMinutes(config.get("model.timeout").asInt().orElse(30)))
                .build();
    }
}
