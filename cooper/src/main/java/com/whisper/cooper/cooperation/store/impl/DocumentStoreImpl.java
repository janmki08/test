package com.whisper.cooper.cooperation.store.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.whisper.cooper.cooperation.model.DocumentModel;
import com.whisper.cooper.cooperation.store.DocumentStore;
import com.whisper.cooper.document.formatter.DocumentFormatter;

public class DocumentStoreImpl extends DocumentStore {

    // DocId -> DocState
    private final ConcurrentHashMap<String, DocumentModel> store = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> userIdToDocIdMap = new ConcurrentHashMap<>();

    public DocumentStoreImpl(Supplier<DocumentFormatter> documentFormatterFactory) {
        super(documentFormatterFactory);
    }

    @Override
    public String toString() {
        return "DocumentStoreImpl{" +
                "store=" + store +
                ", userIdToDocIdMap=" + userIdToDocIdMap +
                '}';
    }

    @Override
    public DocumentModel addEmptyDocument(String userId, String docId) {
        DocumentModel newDocState = new DocumentModel(docId, documentFormatterFactory.get(), getOT());
        store.put(docId, newDocState);
        addCollaboratorToDocument(userId, docId);
        return newDocState;
    }

    @Override
    public DocumentModel getDocumentFromDocId(String docId) {
        return store.get(docId);
    }

    @Override
    public DocumentModel removeDocument(String docId) {
        return store.remove(docId);
    }

    @Override
    public void addCollaboratorToDocument(String userId, String docId) {
        userIdToDocIdMap.put(userId, docId);
        getDocumentFromDocId(docId).incrementCollaboratorCount();
    }

    @Override
    public DocumentModel removeCollaboratorFromDocument(String userId) {
        var doc = getDocumentFromUserId(userId);
        int newCount = doc.decrementCollaboratorCount();
        if (newCount == 0) {
            removeDocument(doc.getId());
        }
        return doc;
    }

    @Override
    public boolean hasDocument(String docId) {
        return store.containsKey(docId);
    }

    @Override
    public DocumentModel getDocumentFromUserId(String userId) {
        return store.get(userIdToDocIdMap.get(userId));
    }


}
