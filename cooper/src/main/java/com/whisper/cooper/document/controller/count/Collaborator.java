package com.whisper.cooper.document.controller.count;

import org.springframework.beans.factory.annotation.Autowired;

import com.whisper.cooper.cooperation.model.messageout.CollaboratorPayload;
import com.whisper.cooper.cooperation.model.messagepush.MessagePush;

// 협업자 수 변경 사항 클라이언트로 통보
public class Collaborator {

    @Autowired
    public MessagePush messageRelayer;

    // docId는 변경 사항을 알려줄 문서 ID
    // Relayer로 변경 사항을 메시지로 전달(메시지 타입은 collaborator_count로 지정)
    public void notifyCount(String docId, CollaboratorPayload collaborationCount) {
        messageRelayer.push("collaborator_count", docId, collaborationCount);
    }

}
