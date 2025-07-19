package de.hotiovip.chatAppBackend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendRequest {
    private String threadId;
    private String message;
    private byte[] file;
}
