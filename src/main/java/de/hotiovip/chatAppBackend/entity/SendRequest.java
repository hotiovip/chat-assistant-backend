package de.hotiovip.chatAppBackend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendRequest {
    private String message;
    private byte[] file;
}
