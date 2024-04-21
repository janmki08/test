package com.whisper.cooper.cooperation.websocket;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import com.whisper.cooper.cooperation.store.DocumentStore;

// 연결 요청 검사, 핸드쉐이크 허용 or 거부
@Component
public class HandshakeInterceptorImpl implements HandshakeInterceptor {

    private final DocumentStore documentStore;

    public HandshakeInterceptorImpl(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    // 웹소켓 핸드쉐이크 이전에 호출됨
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

    	// 쿼리 스트링 가져와서 파싱, 비었으면 핸드쉐이크 허용
    	// 쿼리 스트링이 id=문서ID 형식이 아니면 404 코드 응답 핸드쉐이크 중단
        var q = request.getURI().getQuery();
        if (q == null || q.isBlank() || q.isEmpty()) return true;
        String[] parts = q.split("=");
        if (parts.length != 2 || !parts[0].equals("id")) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.close();
            return false;
        }
        // 문서 저장소에 주어진 문서ID를 가진 문서 확인 후 없으면 404 코드 응답 핸드쉐이크 중단
        var hasDoc = documentStore.hasDocument(parts[1]);
        if (!hasDoc) {
            response.setStatusCode(HttpStatus.NOT_FOUND);
            response.close();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
