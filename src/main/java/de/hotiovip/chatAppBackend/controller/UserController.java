package de.hotiovip.chatAppBackend.controller;

import de.hotiovip.chatAppBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/thread/create")
    public ResponseEntity<String> createThread() {
        Optional<String> threadId = userService.createThread();
        return threadId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.internalServerError().build());
    }

    @GetMapping("/thread/get")
    public ResponseEntity<List<String>> getThreads() {
        Optional<List<String>> threadsId = userService.getThreads();
        return threadsId.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(null));
    }
}
