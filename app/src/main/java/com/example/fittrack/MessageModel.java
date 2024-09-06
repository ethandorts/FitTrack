package com.example.fittrack;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class MessageModel {
    @PropertyName("sender")
    private String sender;
    @PropertyName("recipient")
    private String recipient;
    @PropertyName("message")
    private String message;

    @PropertyName("timestamp")
    private Timestamp timestamp;
    public MessageModel () {}
    public MessageModel(String sender, String recipient, String message, Timestamp timestamp) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessageContent(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}

