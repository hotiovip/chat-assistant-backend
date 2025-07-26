package de.hotiovip.chatAppBackend.controller;

import de.hotiovip.chatAppBackend.entity.ChatMessage;
import de.hotiovip.chatAppBackend.service.ChatService;
import io.github.sashirestela.openai.domain.assistant.ThreadRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/{threadId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable String threadId) {
        Optional<List<ChatMessage>> messages = chatService.getThreadMessages(threadId);
        return messages.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{threadId}/status")
    public ResponseEntity<ThreadRun.RunStatus> getRunStatus(@PathVariable String threadId, @RequestParam String runId) {
        return ResponseEntity.ok(chatService.getRunStatus(threadId, runId));
    }

    @PostMapping("/{threadId}/send")
    public ResponseEntity<String> send(@PathVariable String threadId, @RequestBody ChatMessage chatMessage) {
        Optional<String> response = chatService.send(threadId, chatMessage);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(""));
    }
}