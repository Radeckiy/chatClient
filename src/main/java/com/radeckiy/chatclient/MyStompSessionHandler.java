package com.radeckiy.chatclient;

import com.radeckiy.chatclient.models.Message;
import com.radeckiy.chatclient.models.MessageType;
import com.sun.javafx.util.Logging;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;
import java.util.logging.Logger;

public class MyStompSessionHandler extends StompSessionHandlerAdapter {
    private String username;
    private Logger log = Logger.getLogger(Logging.class.getName());

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
}
