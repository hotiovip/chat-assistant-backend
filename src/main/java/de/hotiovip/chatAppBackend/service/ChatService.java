package de.hotiovip.chatAppBackend.service;

import de.hotiovip.chatAppBackend.entity.SendRequest;
import io.github.sashirestela.cleverclient.client.OkHttpClientAdapter;
import io.github.sashirestela.openai.SimpleOpenAI;
import io.github.sashirestela.openai.common.tool.Tool;
import io.github.sashirestela.openai.domain.assistant.*;
import io.github.sashirestela.openai.domain.assistant.Thread;
import io.github.sashirestela.openai.domain.assistant.Attachment.AttachmentTool;
import io.github.sashirestela.openai.domain.chat.ChatMessage.UserMessage;
import io.github.sashirestela.openai.domain.chat.ChatRequest;
import io.github.sashirestela.openai.domain.file.FileRequest;
import io.github.sashirestela.openai.domain.file.FileResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Value("${openai.key}")
    private String openAIKey;
    private SimpleOpenAI openAIClient;
    private String assistantId;

    @PostConstruct
    public void init() {
        if (openAIKey == null || openAIKey.isEmpty()) {
            logger.error("OpenAI key is null or empty");
            return;
        }

        openAIClient = SimpleOpenAI.builder()
                .apiKey(openAIKey)
                .clientAdapter(new OkHttpClientAdapter())
                .build();

        assistantId = openAIClient.assistants().create(AssistantRequest.builder()
                        .name("Personal AI assistant")
                        .model("gpt-3.5-turbo")
                        .instructions("You are a personal AI assistant")
                        .description("General AI assistant")
                .build()).join().getId();
    }

    private boolean isInitialized() {
        if (openAIClient == null) {
            logger.error("Cannot send messages if OpenAI client is not initialized");
            return false;
        }
        else return true;
    }

    public String createThread() {
        Thread thread = openAIClient.threads().create(ThreadRequest.builder().build()).join();
        return thread.getId();
    }

    public Optional<String> send(SendRequest sendRequest) {
        if (!isInitialized()) return Optional.empty();

        // Try to upload file
        Optional<String> fileId = uploadFile(sendRequest.getFile());
        if (fileId.isEmpty()) return Optional.empty();

        Attachment attachment = Attachment.builder()
                .fileId(fileId.get())
                .tool(AttachmentTool.FILE_SEARCH)
                .build();

        ThreadMessageRequest threadMessageRequest = ThreadMessageRequest.builder()
                .role(ThreadMessageRole.USER)
                .content(sendRequest.getMessage())
                .attachment(attachment)
                .build();
        openAIClient.threadMessages().create(sendRequest.getThreadId(), threadMessageRequest);

        ThreadRunRequest threadRunRequest = ThreadRunRequest.builder()
                .assistantId(assistantId)
                .build();
        ThreadRun threadRun = openAIClient.threadRuns().create(sendRequest.getThreadId(), threadRunRequest).join();

        return Optional.of(threadRun.getId());
    }

    private Optional<String> uploadFile(byte[] file) {
        try {
            // Create temporary file
            Path tempFile = Files.createTempFile("upload-", ".bin");
            Files.write(tempFile, file);

            // Create file request
            FileRequest fileRequest = FileRequest.builder()
                    .file(tempFile)
                    .purpose(FileRequest.PurposeType.ASSISTANTS)
                    .build();

            // Send file request
            CompletableFuture<FileResponse> fileObject = openAIClient.files().create(fileRequest);
            FileResponse fileResponse = fileObject.join();

            // Delete temp file after using it
            Files.deleteIfExists(tempFile);

            return Optional.of(fileResponse.getId());
        }
        catch (IOException e) {
            logger.error("Exception while saving uploaded file: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }
}
