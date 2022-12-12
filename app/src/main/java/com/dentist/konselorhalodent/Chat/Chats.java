package com.dentist.konselorhalodent.Chat;

public class Chats {

    private String id,name,photo,lastMessage,lastMessageTime;

    public Chats() {
    }

    public Chats(String id,String lastMessageTime) {
        this.id = id;
        this.lastMessageTime = lastMessageTime;
    }

    public Chats(String id,String name,String photo,String lastMessage,String lastMessageTime) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
