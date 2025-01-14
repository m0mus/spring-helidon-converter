package net.dmitrykornilov.converter;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface FileTypeService {
    @UserMessage(
"""
### File name
{{fileName}}
        
### File content
{{fileContent}}
""")
    String identifyFileType(@V("fileName") String fileName, @V("fileContent") String fileContent);
}
