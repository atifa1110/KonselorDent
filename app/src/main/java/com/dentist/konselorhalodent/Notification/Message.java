package com.dentist.konselorhalodent.Notification;

import java.util.List;

public class Message {
    private List<String> tokens;
    private Notification notification;
    private Data data;

    public Message(List <String> tokens, Notification notification, Data data) {
        this.tokens = tokens;
        this.notification = notification;
        this.data = data;
    }

    public List<String> getTo() {
        return tokens;
    }

    public void setTo(List<String> tokens) {
        this.tokens = tokens;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
