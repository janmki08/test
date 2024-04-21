(	// 에디터에서 내용 수정, 붙여넣기 할때 이벤트 감지하고 필요한 요소를 초기화
	function () {

	// URL, https http 구분, 호스트 정보 가져오기
    let origin = window.location.origin
    let wsprotocol = window.location.protocol === "https:" ? "wss" : "ws"
    let host = window.location.host

	// 문서, 사용자 ID 초기화
    let docId = null
    let userId = null

    document.getElementById("editor").oninput = onChangeText
    document.getElementById("editor").onpaste = onPaste

	// 문서 상태 관리(에디터 업뎃)
    let docState = new DocState((newDoc) => {
        document.getElementById("editor").value = newDoc
    })

	// 텍스트 입력하는 곳에서 커서 위치를 가져오는 함수
    function getCaretPosition(textarea) {

        if (document.selection) {
            textarea.focus()
            var range = document.selection.createRange()
            var rangeLen = range.text.length
            range.moveStart('character', -textarea.value.length)
            var start = range.text.length - rangeLen
            return {
                'start': start,
                'end': start + rangeLen
            }
        }
        else if (textarea.selectionStart || textarea.selectionStart == '0') {
            return {
                'start': textarea.selectionStart,
                'end': textarea.selectionEnd
            }
        } else {
            return {
                'start': 0,
                'end': 0
            }
        }

    }

	// 작업이 인식 되었을 때 호출되고 새 작업이 이전 작업보다 최신인지 확인하고 맞다면 새 작업을 인식, 보류 중인 작업을 서버로 보냄
    function onOperationAcknowledged(operation, revision) {
		// 새 작업이 이전 작업보다 최신일 경우
        if (docState.lastSyncedRevision < revision) {

            docState.acknowledgeOperation(
                operation,
                revision,
                (pendingOperation) => { // 보류 중인 작업을 서버로 보냄
                    sendOperation(pendingOperation, docState.lastSyncedRevision)
                }
            )

        }
    }

	// 서버한테 받은 페이로드 처리(현재 사용자가 보낸 건지 다른 사용자인지 확인하고 처리)
    function handleOperation(payload) {

        let ack = payload.acknowledgeTo
        let operation = payload.operation
        let revision = payload.revision

		// userId와 현재 사용자가 일치하는지 확인 -> 작업 인식
        if (ack === userId) {

            onOperationAcknowledged(operation, revision)

        } else {	// 현재 사용자가 아닌 경우

            docState.transformPendingOperations(operation, revision)
            docState.lastSyncedRevision = revision
			// 현재 사용자의 작업에 대해 서버로 부터 받은 작업 변환
            transformedOperation = docState.transformOperationAgainstLocalChanges(operation)
			// 변환된 작업 실패한 경우 함수 실행 종료
            if (transformedOperation === null) return
			// 성공한 경우 insert나 delete 함수 호출
            if (transformedOperation.opName === "ins") {
                onInsert(transformedOperation.operand, transformedOperation.position, revision)
            } else if (transformedOperation.opName === "del") {
                onDelete(transformedOperation.operand, transformedOperation.position, revision)
            }

        }
    }

	// 협업자 수 업뎃
    function handleCollaboratorCount(payload) {
        let count = payload.count;
        setCollaboratorCount(count)
    }

    function setCollaboratorCount(count) {
        let collaboratorCount = count - 1
        let text = ""
        if (collaboratorCount === 1) {
            text = "나와 +1의 사용자가 있습니다.";
        } else if (collaboratorCount > 1) {
            text = '나와 + ${collaboratorCount}의 사용자가 있습니다.';
        }
        document.getElementById("collaborator_count").innerText = text
    }

	// STOMP 클라이언트가 특정 문서 업뎃을 구독하게 함(/topic/doc/${docId})
    function subscribeToDocumentUpdates(client, docId) {

        client.subscribe(`/topic/doc/${docId}`, function (message) {
			// 위 주제를 구독하고 여기서 수신된 메시지(JSON 형식)를 파싱하여 type이랑 payload 추출
            let body = message.body;
            let parsed = JSON.parse(body);

            let type = parsed.type;
            let payload = parsed.payload;
			// 삽입 삭제 처리 또는 협업자 수 처리
            switch (type) {
                case "operation":
                    handleOperation(payload)
                    break;
                case "collaborator_count":
                    handleCollaboratorCount(payload)
                    break
            }

        })

    }

	// 새 문서 생성 시
    async function onNewDocument(client) {
		// http post 요청(userId 포함)
        let response = await axios.post(`${origin}/doc/create`, {
            'userId': userId
        })
        // let response = await axios.get(`${httpProtocol}://${serverAddress}:${serverPort}/doc/create`)
        let data = response.data
        docId = data.docId
        // userId = data.userId
		// 생성된 문서 ID를 포함한 링크 표시
        document.getElementById("shareable_link").textContent = `${origin}?id=${docId}`
        subscribeToDocumentUpdates(client, docId)	// 반환된 문서 ID로 클라이언트가 새 문서의 업뎃을 구독하게 함수 호출
    }

	// 웹소켓 클라이언트가 문서에 참여, 위 함수와 유사
    async function onDocumentJoin(client, id) {
		
        let response = await axios.post(`${origin}/doc/${id}`, {
            'userId': userId
        })
        let data = response.data
        let hasError = data.hasError
		// 문서 없을 경우
        if (hasError) { throw 'No such document' }

        docId = id
        // userId = data.userId
        docState.lastSyncedRevision = data.documentRevision
        docState.setDocumentText(data.text || "")
		// + 협업자 수 설정 함수
        document.getElementById("editor").value = docState.document
        document.getElementById("shareable_link").textContent = `${origin}?id=${docId}`
        setCollaboratorCount(data.collaboratorCount)

        subscribeToDocumentUpdates(client, docId)

    }

	// 연결 성공 시 호출
    async function onConnect(client, id) {

        if (!id) {
            await onNewDocument(client) // 새 문서 생성(id가 없을 때)
        } else {
            await onDocumentJoin(client, id) // 문서 참여(이미 id가 존재할 때)
        }

    }

	// 서버에 연결 or 문서 참여
    function connectOrJoin() {
		// URL에서 쿼리(문서 ID) 읽어오기
        let currUrl = window.location.search
        let urlParams = new URLSearchParams(currUrl)
        let id = urlParams.get("id")
		// 웹소켓 프로토콜로 서버 연결 URL 구성
        let url = `${wsprotocol}://${host}/relay${id ? `?id=${id}` : ""}`

		// STOMP 클라이언트를 사용해서 URL로 연결 시도
        let client = Stomp.client(url)
        client.connect(

            {},
			// 연결 성공하면 서버에게 받은 프레임에서 사용자 이름을 추출하고 userId에 할당
            (frame) => {
                let headers = frame.headers
                let userName = headers["user-name"]
                userId = userName
                onConnect(client, id)
            },
			// 연결 실패시 오류
            (err) => {
                document.getElementById("hero").innerHTML = `<p>404<br>The requested page was not found<br>Also, sorry for this awful error page :(</p>`
            },

        )


    }

	// 사용자가 텍스트를 붙여넣을 때 발생하는 이벤트
    function onPaste(param) {
		// 에디터 가져오기
        let editor = document.getElementById("editor")
        let { start, end } = getCaretPosition(editor)	// 붙여넣은 텍스트 삽입 위치(커서)
        let pastedText = param.clipboardData.getData("text")	// 텍스트 가져오기

        // 선택된 텍스트가 있으면 해당 영역 삭제
        if (start !== end) {
            let substr = editor.value.substring(start, end)
            sendDeleteOperation(start, substr)
        }

        // 시작위치에 1 더해서 텍스트 삽입(덮어쓰기 방지)
        sendInsertOperation(start + 1, pastedText)

    }

	// 에디터에 입력할 때마다 실행
    function onChangeText(event) {

        let inputType = event.inputType

        let editor = document.getElementById("editor")
        let currText = editor.value
        let prevText = docState.document
        let { start, end } = getCaretPosition(editor)

        if (inputType === "insertText" || inputType === "insertCompositionText") {

            // 선택된 텍스트 삭제
            if (currText.length <= prevText.length) {
                let charsToDeleteAfterStart = prevText.length - currText.length
                let substr = prevText.substring(start - 1, start + charsToDeleteAfterStart)
                sendDeleteOperation(start - 1, substr)
            }

            sendInsertOperation(start, currText.substring(start - 1, start))
			// 엔터 키 누를 때(줄바꿈)
        } else if (inputType === "insertLineBreak") {

            sendInsertOperation(start, currText.substring(start - 1, start))
			// 텍스트 삭제할 때, 삭제된 텍스트를 서버에 전송하여 동기화
        } else if (inputType === "deleteByCut" || inputType === "deleteContentBackward" || inputType === "deleteContentForward") {

            let charactersDeleted = prevText.length - currText.length
            let deletedString = prevText.substring(start, start + charactersDeleted)
            sendDeleteOperation(start, deletedString)

        } else {
            // 미완
        }

    }

	// 작업 페이로드(-> 서버(문서 상태))
    function createOperationPayload(operation, revision) {
        return {
            'operation': { 'opName': operation.opName, 'operand': operation.operand, 'position': operation.position },
            'revision': revision, 'from': userId
        }
    }

	// 작업, 리비전(버전)을 서버로 전송(문서 업뎃)
    async function sendOperation(operation, revision) {

        if (operation.opName === "ins" || operation.opName === "del") {

            let body = createOperationPayload(operation, revision)

            await axios.post(`${origin}/enqueue/${docId}`, body)

        } else {
			// 미완
        }

    }

	// 삽입 작업 처리(-> 서버)
    function sendInsertOperation(caretPosition, substring) {
		// 작업 큐에 추가
        docState.queueOperation(

            new TextOperation("ins", substring, caretPosition - 1, docState.lastSyncedRevision),
						// 삽입 수행(커서 위치에서 주어진 부분)
            (currDoc) => insertSubstring(currDoc, substring, caretPosition - 1),
			// 서버로 전송(비동기적)
            async (operation, revision) => { await sendOperation(operation, revision) }

        )

    }

	// 삭제 작업 처리(-> 서버)
    function sendDeleteOperation(caretPosition, substring) {
		// 작업 큐에 추가
        docState.queueOperation(
			
            new TextOperation("del", substring, caretPosition, docState.lastSyncedRevision),
						// 삭제 수행(커서 위치에서 주어진 부분)
            (currDoc) => removeSubstring(currDoc, caretPosition, caretPosition + substring.length),

            async (operation, revision) => { await sendOperation(operation, revision) }

        )

    }

	// 문자열에 다른 문자열 삽입
    function insertSubstring(mainString, substring, pos) {
        if (typeof (pos) == "undefined") {
            pos = 0;
        }
        if (typeof (substring) == "undefined") {
            substring = "";
        }
        // pos 위치에 substring 문자열 삽입
        return mainString.slice(0, pos) + substring + mainString.slice(pos);
    }

	// 문자열 제거
    function removeSubstring(str, start, end) {
        return str.substring(0, start) + str.substring(end);
    }

	// 텍스트 입력 시 호출, 편집기에 표시
    function onInsert(charSequence, position, revision) {
        docState.setDocumentText(insertSubstring(docState.document, charSequence, position))

        let editor = document.getElementById("editor")
        editor.value = docState.document

        editor.style.height = "auto";
        let scrollHeight = editor.scrollHeight;
        editor.style.height = `${scrollHeight}px`;

    }
	// 텍스트 삭제 시 호출, 편집기에 표시
    function onDelete(charSequence, position, revision) {
        docState.setDocumentText(removeSubstring(docState.document, position, charSequence.length + position))

        let editor = document.getElementById("editor")
        editor.value = docState.document

        editor.style.height = "auto";
        let scrollHeight = editor.scrollHeight;
        editor.style.height = `${scrollHeight}px`;
    }
	// 웹 소켓 연결 시도, 실패 시 예외 처리
    try {
        connectOrJoin()
    } catch (e) {
        document.getElementById("hero").innerHTML = `<p>로딩 실패</p>`
    }


}).call(this)