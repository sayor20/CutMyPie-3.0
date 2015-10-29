package com.sayor.org.cutmypie.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {


    public String getUserId() {
        return getString("userId");
    }

    public String getReceiverId() {
        return getString("ReceiverId");
    }

    public String getUserName() {
        return getString("userName");
    }

    public String getReceiverName() {
        return getString("ReceiverName");
    }

    public String getBody() {
        return getString("body");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public void setReceiverId(String ReceiverId) {
        put("ReceiverId", ReceiverId);
    }

    public void setUserName(String userName) {
        put("userName", userName);
    }

    public void setReceiverName(String ReceiverName) {
        put("ReceiverName", ReceiverName);
    }

    public void setBody(String body) {
        put("body", body);
    }

}