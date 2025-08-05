package de.hotiovip.chatAppBackend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class Config {
    @Value("${openai.api-key}")
    private String apiKey;
    @Value("${openai.assistant-id}")
    private String assistantId;
}
