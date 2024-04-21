package com.whisper.cooper.relay.relayer;

import org.springframework.beans.factory.annotation.Autowired;

import com.whisper.cooper.cooperation.model.messageout.OQOutPayload;
import com.whisper.cooper.cooperation.model.messagepush.MessagePush;

// 작업 전달용 릴레이어
public class OperationRelayer {

    @Autowired
    public MessagePush messageRelayer;

    // 작업 유형 operation, 해당 문서 ID와 출력 페이로드 전달
    public void relay(String docId, OQOutPayload outPayload) {
        messageRelayer.push("operation", docId, outPayload);
    }

}
