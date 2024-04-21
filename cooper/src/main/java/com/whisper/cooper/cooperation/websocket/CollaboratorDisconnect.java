package com.whisper.cooper.cooperation.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.whisper.cooper.cooperation.model.messageout.CollaboratorPayload;
import com.whisper.cooper.cooperation.store.DocumentStore;
import com.whisper.cooper.document.controller.count.Collaborator;

// 사용자가 세션 끊으면 협업자 목록에서 제거하고 협업자 수가 변경된 경우 알림
@Component
public class CollaboratorDisconnect implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    public DocumentStore documentStore;

    @Autowired
    public Collaborator collaboratorCountNotifier;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        String userId = event.getUser().getName();
        var doc = documentStore.removeCollaboratorFromDocument(userId);
        if (doc.getCollaboratorCount() > 0) {
            collaboratorCountNotifier.notifyCount(doc.getId(), new CollaboratorPayload(doc.getCollaboratorCount()));
        }
    }

}
