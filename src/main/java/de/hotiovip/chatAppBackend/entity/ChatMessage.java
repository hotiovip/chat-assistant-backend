package de.hotiovip.chatAppBackend.entity;

import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.common.content.ContentPart.*;
import io.github.sashirestela.openai.domain.assistant.ThreadMessage;
import io.github.sashirestela.openai.domain.assistant.ThreadMessageRole;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChatMessage {
    private ThreadMessageRole role;
    private List<String> contentList;

    public ChatMessage() {}

    public ChatMessage(ThreadMessage threadMessage) {
        for(ContentPart contentPart : threadMessage.getContent()) {
            this.contentList = new ArrayList<>();

            if (contentPart instanceof ContentPartTextAnnotation contentPartTextAnnotation)
            {
                this.contentList.add(contentPartTextAnnotation.getText().getValue());
            }
        }
        this.role = threadMessage.getRole();
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "role=" + role +
                ", contentList=" + contentList +
                '}';
    }
}
