package com.whisper.cooper.cooperation.oq.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.whisper.cooper.cooperation.model.DocumentModel;
import com.whisper.cooper.cooperation.model.OQInPayload;
import com.whisper.cooper.cooperation.model.messageout.OQOutPayload;
import com.whisper.cooper.cooperation.oq.OQ;
import com.whisper.cooper.cooperation.store.DocumentStore;
import com.whisper.cooper.relay.relayer.OperationRelayer;

// 작업 큐에 작업 추가
public class OQImpl implements OQ {

    @Autowired
    private DocumentStore documentStore;

    @Autowired
    private OperationRelayer operationRelayer;

    // OQ따라 작업을 큐에 추가
    @Override
    public void enqueue(OQInPayload message) {
    	// DocumentStore에서 문서 가져오기
        DocumentModel doc = documentStore.getDocumentFromDocId(message.getDocId());

        int serverDocRevision = doc.getRevision();
        int messageDocRevision = message.getRevision();

        // 서버 문서 리비전과 클라이언트가 보낸 리비전 번호 비교
        if (messageDocRevision < serverDocRevision) {
        	// 클라이언트 문서 버전이 오래되면 서버 문서 버전 이후의 모든 커밋된 리비전에 대해 변환
            var transformedOperations = doc.transformAgainstRevisionLogs(message.getOperation(), messageDocRevision);
            if (transformedOperations == null || transformedOperations.isEmpty()) {
                return;
            }

            for (var operation : transformedOperations) {
                if (operation == null) continue;
                operationRelayer.relay(message.getDocId(), new OQOutPayload(
                        message.getFrom(),
                        operation,
                        doc.getRevision() + 1
                ));
                doc.applyOperation(operation);
            }

        } else if (messageDocRevision == serverDocRevision) {

            operationRelayer.relay(message.getDocId(), new OQOutPayload(
                    message.getFrom(),
                    message.getOperation(),
                    doc.getRevision() + 1
            ));

            doc.applyOperation(message.getOperation());
        }
    }

}
