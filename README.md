# Spring to Helidon Converter

This project provides an application and a set of customizable prompts to convert Maven-based Spring Boot projects into Helidon MP projects.

The application leverages OpenAI APIs for communication with LLMs and is compatible with OpenAI models such as `gpt-4o-mini` and `gpt-4o`. It can also work with other models adhering to OpenAI's API, such as `Qwen Coder` in LM Studio.

## Features
 
- Supports customizable prompts for identifying and converting files.
- Compatible with a wide range of LLMs using OpenAI APIs.
- Allows prompt and configuration updates without requiring recompilation.
- Suitable for converting any Java project with appropriate prompt adjustments.

## How It Works

Each file in the project being converted goes through a two-step process:

### 1. Identify File Type

The file is sent to the LLM to determine its type, such as REST Controller, Spring Data Repository, JPA Entity, etc. The file types are described in the `config/prompt_identify-file-type.txt` file, which can be modified to suit your needs.

### 2. Convert the File

Based on the identified file type, the corresponding prompt is retrieved from the configuration and sent, along with the file content, to the LLM for conversion. The resulting output is saved to the destination directory.

## Usage

### 1. Build the Project
Ensure you have Java 21 or higher and Maven installed.

```bash
mvn package
```

### 2. Configure the Application

Edit the `./config/converter.properties` file to set the following:
- `project-root`: Path to the source Spring Boot project.
- `destination-root`: Path to save the converted Helidon project.
- `model.api-key`: Your OpenAI API key.

### 3. Run the Application

```bash
java -jar target/converter-1.0-SNAPSHOT.jar ./config
```

## Configuration

The converter is highly configurable. Prompts and LLM properties can be edited without recompiling the application. This flexibility allows you to adapt the tool to convert any Java project into another type by modifying the prompts.

Configuration files are located in the `./config` directory:

- `converter.properties`: Main configuration file.
- `prompt_identify-file-type.txt`: Instructions for identifying file types.
- `prompt_<file-type>.txt`: Prompts for converting specific file types.

### Adding or Modifying File Types

To add or delete file types, update `prompt_identify-file-type.txt` and create the corresponding prompt files. For example, to add a `DTO` file type:

1. Add instructions for identifying DTOs in `prompt_identify-file-type.txt`:

    ```text
    7. DTO: Explain how to identify this file type here.
    8. UNIDENTIFIED: File is not a Java file or doesn't match any of the above criteria.
    ```

2. Create a new file, `prompt_dto.txt`, with detailed instructions for converting DTO files.
