package de.hotiovip.chatAppBackend.service;

import de.hotiovip.chatAppBackend.component.OpenAIProvider;
import de.hotiovip.chatAppBackend.entity.ThreadsInformation;
import de.hotiovip.chatAppBackend.entity.User;
import de.hotiovip.chatAppBackend.repository.UserRepository;
import io.github.sashirestela.openai.domain.assistant.Thread;
import io.github.sashirestela.openai.domain.assistant.ThreadRequest;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing user entities.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OpenAIProvider openAIProvider;

    @Autowired
    public UserService(UserRepository userRepository, OpenAIProvider openAIProvider) {
        this.userRepository = userRepository;
        this.openAIProvider = openAIProvider;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<String> createThread() {
        // Create a thread
        Thread thread = openAIProvider.getOpenAIClient().threads().create(ThreadRequest.builder().build()).join();

        // Assign the thread to the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            if (user.getThreadsInformation() instanceof ThreadsInformation threadsInformation) {
                if (threadsInformation.getThreadIds() instanceof List<String> threadIds) {
                    threadIds.add(thread.getId());
                    threadsInformation.setThreadIds(threadIds);
                    return Optional.of(thread.getId());
                }
            }
        }

        return Optional.empty();
    }
    public Optional<List<String>> getThreads() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof User user) {
            ThreadsInformation threadsInformation = user.getThreadsInformation();
            if (threadsInformation != null) {
                return Optional.ofNullable(threadsInformation.getThreadIds());
            }
        }

        // Will return this if the above conditions are not met
        return Optional.empty();
    }

}