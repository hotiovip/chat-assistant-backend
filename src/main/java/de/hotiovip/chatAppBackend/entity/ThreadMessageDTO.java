package de.hotiovip.chatAppBackend.entity;

import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.domain.assistant.ThreadMessage;
import io.github.sashirestela.openai.domain.assistant.ThreadMessageRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ThreadMessageDTO {
    private ThreadMessageRole role;
    private List<String> contentList;

    public ThreadMessageDTO(ThreadMessage threadMessage) {
        for(ContentPart contentPart : threadMessage.getContent()) {
            this.contentList.add(contentPart.toString());
        }
        this.role = threadMessage.getRole();
    }
}
