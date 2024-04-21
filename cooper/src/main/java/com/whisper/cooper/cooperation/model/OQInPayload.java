package com.whisper.cooper.cooperation.model;

public class OQInPayload {

    private String docId;
    private int revision;
    private String from;
    private TextOperation operation;

    public OQInPayload() {
    }

    public OQInPayload(String docId, int revision, String from, TextOperation operation) {
        this.docId = docId;
        this.revision = revision;
        this.from = from;
        this.operation = operation;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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

    public TextOperation getOperation() {
        return operation;
    }

    public void setOperation(TextOperation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "OQInPayload{" +
                "docId='" + docId + '\'' +
                ", revision=" + revision +
                ", from='" + from + '\'' +
                ", operation=" + operation +
                '}';
    }
}
