package com.whisper.cooper.cooperation.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.whisper.cooper.cooperation.ot.OT;
import com.whisper.cooper.document.formatter.DocumentFormatter;

public class DocumentModel {

    final List<TextOperation> revisionLog = new ArrayList<>();
    private final OT operationTransformations;
    private final String id;
    private final DocumentFormatter documentFormatter;
    private int revision = 0;
    private int collaboratorCount = 0;

    public DocumentModel(String id, DocumentFormatter documentFormatter, OT operationTransformations) {
        this.id = id;
        this.documentFormatter = documentFormatter;
        this.operationTransformations = operationTransformations;
    }

    public int getRevision() {
        return revision;
    }

    public String getDocText() {
        return documentFormatter.getText();
    }

    public void applyOperation(TextOperation operation) {
        documentFormatter.applyOperation(operation);
        revisionLog.add(revision, operation);
        revision++;
    }

    public List<TextOperation> transformAgainstRevisionLogs(TextOperation operation, int from) {

        class TextOperationWrapper {

            final TextOperation operation;
            final int transformFrom;

            TextOperationWrapper(TextOperation operation, int transformFrom) {
                this.operation = operation;
                this.transformFrom = transformFrom;
            }
        }

        // 리스트에 변환된 작업 저장
        List<TextOperation> transformedOperations = new ArrayList<>();
        // opQueue 생성, 주어진 작업과 변환 시작할 리비전 번호(from) 큐에 추가
        Queue<TextOperationWrapper> opQueue = new LinkedList<>();
        opQueue.add(new TextOperationWrapper(operation, from));

        // 큐 빌 때까지 작업 변환 반복
        while (!opQueue.isEmpty()) {

            var op = opQueue.poll();
            var transformedOperation = operation;

            // 현재 작업 리비전 번호부터 모든 리비전 반복
            for (int revision = op.transformFrom; revision < revisionLog.size(); revision++) {
                var operations = operationTransformations.transform(transformedOperation, revisionLog.get(revision));
                if (operations == null || operations.length == 0) {
                    transformedOperation = null;
                    break;
                }
                transformedOperation = operations[0];
                if (operations.length > 1) {
                    opQueue.add(new TextOperationWrapper(operations[1], revision + 1));
                }
            }

            transformedOperations.add(transformedOperation);

        }

        return transformedOperations;

    }

    // 현재 협업자 수 반환
    public int getCollaboratorCount() {
        return collaboratorCount;
    }

    // 협업자 수 증가값 반환
    public int incrementCollaboratorCount() {
        collaboratorCount++;
        return collaboratorCount;
    }

    // 협업자 수 감소값 반환
    public int decrementCollaboratorCount() {
        collaboratorCount--;
        return collaboratorCount;
    }

    // 문서 식별자 반환
    public String getId() {
        return id;
    }
}
