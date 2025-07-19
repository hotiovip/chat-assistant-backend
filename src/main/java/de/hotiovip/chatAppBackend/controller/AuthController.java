package de.hotiovip.chatAppBackend.controller;

import de.hotiovip.chatAppBackend.entity.User;
import de.hotiovip.chatAppBackend.request.AuthRequest;
import de.hotiovip.chatAppBackend.security.JwtUtil;
import de.hotiovip.chatAppBackend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/password/hash")
    public String getHashedPassword(@RequestHeader("password") String password) {
        return passwordEncoder.encode(password);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        // Fetch the user by username
        Optional<User> user = userService.getUserByUsername(request.getUsername());

        // Check if user exists
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not exist");
        }

        // Validate the password
        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        // Generate and return the token
        String token = jwtUtil.generateToken(user.get().getId());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/token/valid")
    public ResponseEntity<Boolean> isTokenValid(@RequestHeader("Authorization") String bearerToken) {
        if (jwtUtil.validateToken(bearerToken)) {
            return ResponseEntity.ok(true);
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }
}