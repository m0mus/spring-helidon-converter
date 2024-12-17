package converter;

import java.nio.file.Path;

import io.helidon.config.Config;
import io.helidon.config.FileConfigSource;

public class App {
    public static void main( String[] args ) throws Exception {
        var configPath = Path.of("");
        if (args.length != 0) {
            configPath = Path.of(args[0]);
        }

        var configFile = configPath.resolve("converter.properties");
        var config = Config.create(FileConfigSource.builder()
                                           .path(configFile)
                                           .build());

        var tools = new ProjectTools(config);
        var converter = ConverterServiceFactory.createConverterService(configPath, config, tools);

        var summary = converter.convert("Convert ALL files in one batch! Don't offer converting some files later!");
        System.out.println(summary);
    }
}
