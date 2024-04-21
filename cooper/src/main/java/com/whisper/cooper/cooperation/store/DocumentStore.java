package com.whisper.cooper.cooperation.store;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import com.whisper.cooper.cooperation.model.DocumentModel;
import com.whisper.cooper.cooperation.ot.OT;
import com.whisper.cooper.document.formatter.DocumentFormatter;

public abstract class DocumentStore {

    final public Supplier<DocumentFormatter> documentFormatterFactory;
    @Autowired
    private OT operationTransformations;

    public DocumentStore(Supplier<DocumentFormatter> documentFormatterFactory) {
        this.documentFormatterFactory = documentFormatterFactory;
    }

    public OT getOT() {
        return operationTransformations;
    }

    // 빈 문서
    public abstract DocumentModel addEmptyDocument(String userId, String docId);
    // 문서 가져오기(문서 ID)
    public abstract DocumentModel getDocumentFromDocId(String docId);
    // 문서 가져오기(사용자 ID)
    public abstract DocumentModel getDocumentFromUserId(String docId);
    // 문서 제거하기
    public abstract DocumentModel removeDocument(String docId);
    // 협업자 문서에 추가
    public abstract void addCollaboratorToDocument(String userId, String docId);
    // 협업자 문서에서 제거
    public abstract DocumentModel removeCollaboratorFromDocument(String userId);
    // 문서 존재 여부 확인
    public abstract boolean hasDocument(String docId);

}
