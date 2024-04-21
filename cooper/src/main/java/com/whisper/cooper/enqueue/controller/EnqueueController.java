package com.whisper.cooper.enqueue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whisper.cooper.cooperation.model.DocumentModel;
import com.whisper.cooper.cooperation.model.OQInPayload;
import com.whisper.cooper.cooperation.oq.OQ;
import com.whisper.cooper.cooperation.store.DocumentStore;
import com.whisper.cooper.enqueue.model.EnqueuePayload;
import com.whisper.cooper.enqueue.model.EnqueueResponse;

// 작업 큐에 작업을 넣음
@RestController
@RequestMapping("/enqueue")
public class EnqueueController {

    @Autowired
    private DocumentStore documentStore;

    @Autowired
    private OQ operationQueue;


    @PostMapping("/{id}")
    private EnqueueResponse enqueue(@PathVariable("id") String id, @RequestBody EnqueuePayload operation) throws Exception {
        DocumentModel doc = documentStore.getDocumentFromDocId(id);
        if (doc == null) {
            return new EnqueueResponse("error", "document with id = " + id + " does not exist");
        } else {

            operationQueue.enqueue(new OQInPayload(
                    id,
                    operation.getRevision(),
                    operation.getFrom(),
                    operation.getOperation())
            );

            // 성공 응답 반환
            return new EnqueueResponse("ok", null);
        }
    }

}
