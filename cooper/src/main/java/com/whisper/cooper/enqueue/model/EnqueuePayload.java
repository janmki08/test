package com.whisper.cooper.enqueue.model;

import com.whisper.cooper.cooperation.model.TextOperation;

public class EnqueuePayload {

    private TextOperation operation;
    private int revision; // 작업이 적용되는 문서 버전 표시
    private String from;

    public EnqueuePayload(TextOperation operation, int revision, String from) {
        this.operation = operation;
        this.revision = revision;
        this.from = from;
    }

    public EnqueuePayload() {
    }

    public TextOperation getOperation() {
        return operation;
    }

    public void setOperation(TextOperation operation) {
        this.operation = operation;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "EnqueuePayload{" +
                "operation=" + operation +
                ", revision=" + revision +
                ", from='" + from + '\'' +
                '}';
    }
}
