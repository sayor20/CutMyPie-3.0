package com.sayor.org.cutmypie.models;

public class Conversation {
    String UserID;
    String UserName;
    String body;

    public Conversation(String UserID,String UserName, String body){
        this.UserID = UserID;
        this.UserName = UserName;
        this.body = body;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
