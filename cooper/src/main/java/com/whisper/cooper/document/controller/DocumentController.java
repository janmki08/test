package com.whisper.cooper.document.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.whisper.cooper.cooperation.model.DocumentModel;
import com.whisper.cooper.cooperation.model.messageout.CollaboratorPayload;
import com.whisper.cooper.cooperation.store.DocumentStore;
import com.whisper.cooper.document.controller.count.Collaborator;
import com.whisper.cooper.document.model.DocumentCreateResponse;
import com.whisper.cooper.document.model.DocumentJoinResponse;
import com.whisper.cooper.document.model.UserModel;

@RestController
@RequestMapping("/doc")
public class DocumentController {

    @Autowired
    private DocumentStore documentStore;

    @Autowired
    private Collaborator collaboratorCountRelayer;

    // POST 요청을 /doc/create 엔드포인트에 매핑
    // 새 문서 생성
    @PostMapping("/create")
    public DocumentCreateResponse createDoc(@RequestBody UserModel user) {

        String docId = NanoIdUtils.randomNanoId();
        documentStore.addEmptyDocument(user.getUserId(), docId);
        return new DocumentCreateResponse(docId);

    }

    // POST 요청을 /doc/{id} 엔드포인트로 매핑(id = 문서 ID)
    // 문서 참여 요청 처리
    @PostMapping("/{id}")
    public DocumentJoinResponse joinDoc(@PathVariable("id") String id, @RequestBody UserModel user) {

        DocumentModel doc = documentStore.getDocumentFromDocId(id);
        if (doc == null) {
            return DocumentJoinResponse.withError("document with id = " + id + " does not exist");
        } else {

            documentStore.addCollaboratorToDocument(user.getUserId(), id);
            collaboratorCountRelayer.notifyCount(id, new CollaboratorPayload(doc.getCollaboratorCount()));

            return DocumentJoinResponse.noError(doc.getCollaboratorCount(), doc.getDocText(), doc.getRevision());
        }

    }

}
