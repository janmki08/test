package com.whisper.cooper.document.model;

public class UserModel {

    private String userId;

    public UserModel(String userId) {
        this.userId = userId;
    }

    public UserModel() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
