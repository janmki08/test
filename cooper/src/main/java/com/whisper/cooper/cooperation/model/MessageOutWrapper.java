package com.whisper.cooper.cooperation.model;

public class MessageOutWrapper<P> {

    private String type;
    private P payload;

    public MessageOutWrapper(String type, P payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageOutWrapper() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public P getPayload() {
        return payload;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }

}
