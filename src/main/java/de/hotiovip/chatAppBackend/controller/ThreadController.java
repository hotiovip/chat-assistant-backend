package de.hotiovip.chatAppBackend.controller;

import de.hotiovip.chatAppBackend.entity.ChatMessage;
import de.hotiovip.chatAppBackend.service.ThreadService;
import io.github.sashirestela.openai.domain.assistant.ThreadRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/threads")
public class ThreadController {
    private final ThreadService threadService;

    @Autowired
    public ThreadController(ThreadService threadService) {
        this.threadService = threadService;
    }

    @PostMapping
    public ResponseEntity<String> createThread() {
        Optional<String> threadId = threadService.createThread();
        return threadId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.internalServerError().build());
    }
    @GetMapping
    public ResponseEntity<List<String>> getThreads() {
        Optional<List<String>> threadsId = threadService.getThreads();
        return threadsId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
    }

    @DeleteMapping("/{threadId}")
    public ResponseEntity<Void> deleteThread(@PathVariable String threadId) {
        boolean response = threadService.deleteThread(threadId);
        if (response) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/{threadId}")
    public ResponseEntity<String> send(@PathVariable String threadId, @RequestBody ChatMessage chatMessage) {
        Optional<String> response = threadService.send(threadId, chatMessage);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(""));
    }

    @GetMapping("/{threadId}/title")
    public ResponseEntity<String> getTitle(@PathVariable String threadId) {
        return threadService.getThreadTitle(threadId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
    @GetMapping("/{threadId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable String threadId) {
        Optional<List<ChatMessage>> messages = threadService.getThreadMessages(threadId);
        return messages.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/{threadId}/status")
    public ResponseEntity<ThreadRun.RunStatus> getRunStatus(@PathVariable String threadId, @RequestParam String runId) {
        return ResponseEntity.ok(threadService.getRunStatus(threadId, runId));
    }
}