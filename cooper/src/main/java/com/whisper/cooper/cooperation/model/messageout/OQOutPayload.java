package com.whisper.cooper.cooperation.model.messageout;

import com.whisper.cooper.cooperation.model.TextOperation;

// 작업의 출력 페이로드
public class OQOutPayload {

    private String acknowledgeTo; // 작업을 보낸 클라이언트 식별
    private TextOperation operation;
    private long revision;

    // 생성자
    public OQOutPayload(String acknowledgeTo, TextOperation operation, long revision) {
        this.acknowledgeTo = acknowledgeTo;
        this.operation = operation;
        this.revision = revision;
    }

    public OQOutPayload() {
    }

    // Getter 메서드
    public String getAcknowledgeTo() {
        return acknowledgeTo;
    }

    // Setter 메서드
    public void setAcknowledgeTo(String acknowledgeTo) {
        this.acknowledgeTo = acknowledgeTo;
    }

    public TextOperation getOperation() {
        return operation;
    }

    public void setOperation(TextOperation operation) {
        this.operation = operation;
    }

    public long getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

}
