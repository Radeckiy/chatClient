package com.radeckiy.chatclient;

import com.radeckiy.chatclient.models.Message;
import com.radeckiy.chatclient.models.MessageType;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private String username;

    public MyStompSessionHandler(String username) {
        this.username = username;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/topic/publicChatRoom", this);
        session.send("/app/chat.sendMessage", new Message(MessageType.JOIN, "", username));
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Message.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        System.err.println("Message: " + payload.toString());
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.err.println("\nНе удалось подключиться к websocket'у! Сервер отключен или у вас нет доступа к этому чату!\n");
    }
}
