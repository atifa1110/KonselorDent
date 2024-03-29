package com.dentist.konselorhalodent.Model;

public class Messages {

    private String messageId;
    private String message;
    private String messageFrom;
    private Long messageTime;
    private String messageType;

    public Messages() {
    }

    public Messages(String message, String messageFrom, Long messageTime, String messageType) {
        this.message = message;
        this.messageFrom = messageFrom;
        this.messageTime = messageTime;
        this.messageType = messageType;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public Long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Long messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
