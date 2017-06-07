package com.javgon.wakeme.Model;

import java.util.Date;

/**
 * Created by javier gonzalez on 6/5/2017.
 * Will be used to keep track of audio messages sent between users.
 * App will look for "inbox" messages, which will include an URL to download audio file from storage
 */

public class AudioMessage {

    String fromUserId;
    String toUserId;
    String messageId;
    boolean opened;
    String uri;     //audio uri stored as a string
    /*
     volatile message will be set after user opens message,
     determines whether audio clip will be saved in storage (pro user) or deleted after listened to
    */
    boolean volatileMessage;
    Date sentAt;


    public AudioMessage(String messageId, String uri, String fromUserId, String toUserId){
        this.messageId=messageId;
        this.fromUserId=fromUserId;
        this.toUserId=toUserId;
        this.uri=uri;
        this.opened=false;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isVolatileMessage() {
        return volatileMessage;
    }

    public void setVolatileMessage(boolean volatileMessage) {
        this.volatileMessage = volatileMessage;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

}
