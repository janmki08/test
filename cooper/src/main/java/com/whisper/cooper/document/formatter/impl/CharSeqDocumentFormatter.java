package com.whisper.cooper.document.formatter.impl;

import com.whisper.cooper.cooperation.model.TextOperation;
import com.whisper.cooper.document.formatter.DocumentFormatter;

public class CharSeqDocumentFormatter implements DocumentFormatter {

    private final StringBuffer buffer = new StringBuffer();

    // 버퍼에 있는 문서 내용 수정
    @Override
    public String applyOperation(TextOperation operation) {
        switch (operation.getOpName()) {
            case "ins":	// 삽입 작업 일때
                return applyInsert(operation);
            case "del": // 삭제 작업 일때
                return applyDelete(operation);
            default:
                return "";
        }
    }

    // 버퍼에 있는 내용을 문자열로 반환
    @Override
    public String getText() {
        return buffer.toString();
    }

    // 삽입 작업을 버퍼에 적용
    private String applyInsert(TextOperation operation) {
    	// 버퍼의 길이와 작업의 위치를 비교해서 맞는 위치에 삽입
        if (buffer.length() == operation.getPosition()) {
            buffer.append(operation.getOperand());
        } else {
            buffer.insert(operation.getPosition(), operation.getOperand());
        }
        return buffer.toString(); // 변경된 문서 내용 반환
    }

    // 삽입과 동일 메커니즘
    private String applyDelete(TextOperation operation) {
        var start = operation.getPosition();
        var end = start + operation.getOperand().length();
        buffer.delete(start, end);
        return buffer.toString();
    }

    
}
