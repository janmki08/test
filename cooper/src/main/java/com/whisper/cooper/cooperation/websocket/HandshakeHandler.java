package com.whisper.cooper.cooperation.websocket;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class HandshakeHandler extends DefaultHandshakeHandler {

	// Principal 객체를 저장하기 위한 키(attributes에서 이 키를 사용해서 저장하고 검색)
    private static final String ATTR_PRINCIPAL = "__principal__";

    // 웹소켓 연결 요청이 있으면 호출되어 사용자 결정
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        final String name;

        // 속성맵에서 ATTR_PRINCIPAL 키가 있으면 해당 키에 저장된 사용자 이름 가져오기
        // 없으면 UUID를 사용해서 랜덤으로 이름 생성 -> 속성맵에 저장
        if (!attributes.containsKey(ATTR_PRINCIPAL)) {
            name = UUID.randomUUID().toString();
            attributes.put(ATTR_PRINCIPAL, name);
        } else {
            name = (String) attributes.get(ATTR_PRINCIPAL);
        }

        return () -> name;
    }

}
