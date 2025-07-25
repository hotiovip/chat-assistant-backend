package de.hotiovip.chatAppBackend.controller;

import de.hotiovip.chatAppBackend.entity.SendRequest;
import de.hotiovip.chatAppBackend.entity.ThreadMessageDTO;
import de.hotiovip.chatAppBackend.service.ChatService;
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
    public ResponseEntity<List<ThreadMessageDTO>> getMessages(@PathVariable String threadId) {
        Optional<List<ThreadMessageDTO>> messages = chatService.getThreadMessages(threadId);
        return messages.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{threadId}/send")
    public ResponseEntity<String> send(@PathVariable String threadId, @RequestBody SendRequest sendRequest) {
        Optional<String> response = chatService.send(threadId, sendRequest);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(""));
    }
}