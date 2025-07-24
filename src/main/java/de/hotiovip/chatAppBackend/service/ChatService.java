package de.hotiovip.chatAppBackend.service;

import de.hotiovip.chatAppBackend.component.OpenAIProvider;
import de.hotiovip.chatAppBackend.entity.SendRequest;
import io.github.sashirestela.openai.domain.assistant.*;
import io.github.sashirestela.openai.domain.assistant.Attachment.AttachmentTool;
import io.github.sashirestela.openai.domain.file.FileRequest;
import io.github.sashirestela.openai.domain.file.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final OpenAIProvider openAIProvider;

    @Autowired
    public ChatService(OpenAIProvider openAIProvider) {
        this.openAIProvider = openAIProvider;
    }

    public Optional<String> send(SendRequest sendRequest) {
        if (!openAIProvider.isInitialized()) return Optional.empty();

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
        openAIProvider.getOpenAIClient().threadMessages().create(sendRequest.getThreadId(), threadMessageRequest);

        ThreadRunRequest threadRunRequest = ThreadRunRequest.builder()
                .assistantId(openAIProvider.getAssistantId())
                .build();
        ThreadRun threadRun = openAIProvider.getOpenAIClient().threadRuns().create(sendRequest.getThreadId(), threadRunRequest).join();

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
            CompletableFuture<FileResponse> fileObject = openAIProvider.getOpenAIClient().files().create(fileRequest);
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
