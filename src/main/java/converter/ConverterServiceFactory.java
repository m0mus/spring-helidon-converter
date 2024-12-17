package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import io.helidon.config.Config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class ConverterServiceFactory {
    private static ConverterService service = null;

    public static ConverterService createConverterService(Path configPath, Config config, ProjectTools tools) {
        if (service != null) {
            return service;
        }

        var model = OpenAiChatModel.builder()
                .apiKey(config.get("OPENAI_API_KEY").asString().orElseThrow())
                .modelName(config.get("model-name").asString().orElse(GPT_4_O_MINI.toString()))
                .timeout(Duration.ofMinutes(config.get("timeout").asInt().orElse(30)))
                .build();

        service = AiServices.builder(ConverterService.class)
                .chatLanguageModel(model)
                .systemMessageProvider(chatModelId -> readPrompt(configPath))
                .tools(tools)
                .build();

        return service;
    }

    private static String readPrompt(Path configPath) {
        Path path = configPath.resolve("prompt.txt");
        try {
            return Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
