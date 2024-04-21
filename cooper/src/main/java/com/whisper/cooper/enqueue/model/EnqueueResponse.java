package com.whisper.cooper.enqueue.model;

public class EnqueueResponse {

    public final String status;
    public final String message;

    public EnqueueResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

}
