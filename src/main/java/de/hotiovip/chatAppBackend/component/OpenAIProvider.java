package de.hotiovip.chatAppBackend.component;

import de.hotiovip.chatAppBackend.Config;
import io.github.sashirestela.cleverclient.client.OkHttpClientAdapter;
import io.github.sashirestela.openai.SimpleOpenAI;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Getter
public class OpenAIProvider {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIProvider.class);

    private final Config config;
    private SimpleOpenAI openAIClient;
    private String assistantId;

    public OpenAIProvider(Config config) {
        this.config = config;
    }

    @PostConstruct
    public void init() {
        String openAIKey = config.getApiKey();
        if (openAIKey == null || openAIKey.isEmpty()) {
            logger.error("OpenAI key is null or empty");
            return;
        }

        openAIClient = SimpleOpenAI.builder()
                .apiKey(openAIKey)
                .clientAdapter(new OkHttpClientAdapter())
                .build();

        assistantId = config.getAssistantId();

//        assistantId = openAIClient.assistants().create(AssistantRequest.builder()
//                .name("Personal AI assistant")
//                .model("gpt-3.5-turbo")
//                .instructions("You are a personal AI assistant")
//                .description("General AI assistant")
//                .build()).join().getId();
    }

    public boolean isInitialized() {
        if (openAIClient == null) {
            logger.error("Cannot send messages if OpenAI client is not initialized");
            return false;
        }
        return true;
    }
}
