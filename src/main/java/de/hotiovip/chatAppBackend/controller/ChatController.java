package de.hotiovip.chatAppBackend.controller;

import de.hotiovip.chatAppBackend.entity.SendRequest;
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

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody SendRequest sendRequest) {
        Optional<String> response = chatService.send(sendRequest);
        return response.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(""));
    }
}