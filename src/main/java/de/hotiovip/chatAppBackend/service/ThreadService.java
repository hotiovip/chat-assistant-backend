package de.hotiovip.chatAppBackend.service;

import de.hotiovip.chatAppBackend.component.OpenAIProvider;
import de.hotiovip.chatAppBackend.entity.ChatMessage;
import de.hotiovip.chatAppBackend.entity.User;
import de.hotiovip.chatAppBackend.repository.UserRepository;
import io.github.sashirestela.openai.common.Page;
import io.github.sashirestela.openai.domain.assistant.*;
import io.github.sashirestela.openai.domain.assistant.Thread;
import io.github.sashirestela.openai.domain.file.FileRequest;
import io.github.sashirestela.openai.domain.file.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ThreadService {
    private static final Logger logger = LoggerFactory.getLogger(ThreadService.class);

    private final OpenAIProvider openAIProvider;
    private final UserRepository userRepository;

    @Autowired
    public ThreadService(OpenAIProvider openAIProvider, UserRepository userRepository) {
        this.openAIProvider = openAIProvider;
        this.userRepository = userRepository;
    }

    public Optional<String> createThread() {
        // Create a thread
        Thread thread = openAIProvider.getOpenAIClient().threads().create(ThreadRequest.builder().build()).join();

        // Assign the thread to the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            List<String> threadIds = user.getThreadIds();
            if (threadIds == null) {
                // Create new list as there is none yet
                threadIds = new ArrayList<>();
            }

            threadIds.add(thread.getId());
            user.setThreadIds(threadIds);
            userRepository.save(user);

            return Optional.of(thread.getId());
        }

        return Optional.empty();
    }
    public Optional<List<String>> getThreads() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            List<String> threadIds = user.getThreadIds();
            if (threadIds != null) {
                return Optional.of(threadIds);
            }
        }

        // Will return this if the above conditions are not met
        return Optional.empty();
    }


    public Optional<List<ChatMessage>> getThreadMessages(String threadId) {
        Optional<List<String>> optionalThreadIds = getThreads();
        if (optionalThreadIds.isEmpty()) { // The user has no threads
            return Optional.empty();
        }
        else if (!optionalThreadIds.get().contains(threadId)) { // The user does not own the given thread
            return Optional.empty();
        }

        List<ChatMessage> chatMessageList = new java.util.ArrayList<>(List.of());
        Page<ThreadMessage> messages = openAIProvider.getOpenAIClient().threadMessages().getList(threadId).join();
        for (ThreadMessage message : messages.getData()) {
            chatMessageList.add(new ChatMessage(message));
        }

        return Optional.of(chatMessageList);
    }

    public Optional<String> send(String threadId, ChatMessage chatMessage) {
        if (!openAIProvider.isInitialized()) return Optional.empty();

        // Try to upload file
//        Optional<String> fileId = uploadFile(sendRequest.getFile());
//        if (fileId.isEmpty()) return Optional.empty();

//        Attachment attachment = Attachment.builder()
//                .fileId(fileId.get())
//                .tool(AttachmentTool.FILE_SEARCH)
//                .build();

        ThreadMessageRequest threadMessageRequest = ThreadMessageRequest.builder()
                .role(ThreadMessageRole.USER)
                .content(chatMessage.getContentList().getFirst())
//                .attachment(attachment)
                .build();
        openAIProvider.getOpenAIClient().threadMessages().create(threadId, threadMessageRequest);

        ThreadRunRequest threadRunRequest = ThreadRunRequest.builder()
                .assistantId(openAIProvider.getAssistantId())
                .build();
        ThreadRun threadRun = openAIProvider.getOpenAIClient().threadRuns().create(threadId, threadRunRequest).join();

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

    public ThreadRun.RunStatus getRunStatus(String threadId, String runId) {
        return openAIProvider.getOpenAIClient().threadRuns().getOne(threadId, runId).join().getStatus();
    }
}
