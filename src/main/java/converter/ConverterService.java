package converter;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ConverterService {
    @UserMessage(
"""
### File content:
{{fileContent}}
""")
    String convert(@MemoryId String fileType, @V("fileContent") String fileContent);
}
