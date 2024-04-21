// 문서 상태 관리
class DocState {

    constructor(onDocumentChange) {
        this.onDocumentChange = onDocumentChange
        this.sentOperation = null // 작업이 서버로 전송 되었지만 미승인 상태
        this.pendingOperations = new Deque() // 작업이 아직 서버로 전송되지 않은 상태
    }

    lastSyncedRevision = 0
    document = ""
    prevText = ""
	// 서버에게 온 응답 처리, 보류 작업 처리
    acknowledgeOperation(operation, newRevision, onPendingOperation) {
        // remove sent operation
        this.sentOperation = null
        this.lastSyncedRevision = newRevision

        // take out a pending operation
        if (!this.pendingOperations.isEmpty()) {
            this.sentOperation = this.pendingOperations.dequeueFront()
            onPendingOperation(this.sentOperation)
        }

    }
	// 문서 내용 설정, 이전 문서 내용 업데이트
    setDocumentText(text) {
        this.prevText = this.document
        this.document = text
    }
	// 새 작업 대기열에 추가, 서버에 전송
    async queueOperation(operation, newDocument, onSend) {

        this.setDocumentText(newDocument(this.document))
        console.log(`[DOC] ${this.document}`)

        if (this.sentOperation === null) {
            this.sentOperation = operation
            console.log(`[SEND] sent operation = ${JSON.stringify(operation)}, lastSyncedRevision = ${operation.revision}`)
            await onSend(operation, this.lastSyncedRevision)
        } else {
            console.log(`[ENQ] enqueued operation = ${JSON.stringify(operation)}, lastSyncedRevision = ${this.lastSyncedRevision}`)
            this.pendingOperations.enqueueRear(operation)
        }

    }
	// 보류중인 작업 변환 및 클라이언트 측 문서 변경 호환
    transformPendingOperations(op2, newRevision) {

        if (op2 === null) { return }
        this.pendingOperations.modifyWhere((op1) => OperationTransformation.transformOperation(op1, op2))

    }
	// 로컬 변경 내용을 서버에 적용하기 전에
	// 서버에 보낼 변경 내용을 이미 보낸 연산과 호환 시킴
    transformOperationAgainstSentOperation(op1) {
        if (this.sentOperation === null) return op1
        let transformed = OperationTransformation.transformOperation(op1, this.sentOperation)
        this.sentOperation = null
        return transformed
    }
	// 클라이언트 측 문서 변경에 대해 작업 변환
    transformOperationAgainstLocalChanges(op1) {
        let transformed = op1
        if (this.sentOperation !== null) {
            transformed = OperationTransformation.transformOperation(transformed, this.sentOperation)
        }
        this.pendingOperations.forEach(op2 => {
            transformed = OperationTransformation.transformOperation(transformed, op2)
        })
        this.sentOperation = null
        return transformed
    }

}

