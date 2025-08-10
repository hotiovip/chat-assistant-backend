package de.hotiovip.chatAppBackend.entity;

import io.github.sashirestela.openai.common.content.ContentPart;
import io.github.sashirestela.openai.common.content.ContentPart.*;
import io.github.sashirestela.openai.domain.assistant.ThreadMessage;
import io.github.sashirestela.openai.domain.assistant.ThreadMessageRole;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ChatMessage {
    private ThreadMessageRole role;
    private List<String> contentList;
    private byte[] attachment;
    private String attachmentName;
    private String attachmentExtension;

    public ChatMessage() {}

    public ChatMessage(ThreadMessage threadMessage) {
        this.role = threadMessage.getRole();

        for(ContentPart contentPart : threadMessage.getContent()) {
            this.contentList = new ArrayList<>();

            if (contentPart instanceof ContentPartTextAnnotation contentPartTextAnnotation)
            {
                this.contentList.add(contentPartTextAnnotation.getText().getValue());
            }
        }

        this.attachment = null;
        this.attachmentExtension = null;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "role=" + role +
                ", contentList=" + contentList +
                ", attachment=" + Arrays.toString(attachment) +
                ", attachmentName='" + attachmentName + '\'' +
                ", attachmentExtension='" + attachmentExtension + '\'' +
                '}';
    }
}
