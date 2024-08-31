package com.example.fittrack;

import com.google.firebase.Timestamp;

public class MessageModel {
    String sender;
    String recipient;
    String message_content;
    Timestamp time_sent;
    String SenderID;

    public MessageModel(String sender, String recipient, String message_content, Timestamp time_sent) {
        this.sender = sender;
        this.recipient = recipient;
        this.message_content = message_content;
        this.time_sent = time_sent;
    }

    public String getMessageSender() {
        return sender;
    }

    public String getMessageRecipient() {
        return recipient;
    }

    public String getMessageContent() {
        return message_content;
    }

    public Timestamp getMessageTimeSent() {
        return time_sent;
    }
}
