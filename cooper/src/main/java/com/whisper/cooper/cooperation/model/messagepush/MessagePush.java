package com.whisper.cooper.cooperation.model.messagepush;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.whisper.cooper.cooperation.model.MessageOutWrapper;

@Component
public class MessagePush {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void push(String type, String docId, Object payload) {
    	// 웹소켓으로 메시지 전송
        simpMessagingTemplate.convertAndSend("/topic/doc/" + docId, new MessageOutWrapper<>(type, payload));
    }

}
// 웹소켓으로 메시지 푸시