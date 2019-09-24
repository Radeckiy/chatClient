package com.radeckiy.chatclient;

import com.radeckiy.chatclient.models.Message;
import com.radeckiy.chatclient.models.MessageType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Application {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        FileInputStream fis;
        Properties property = new Properties();
        // Дефолтные значения
        String host = "localhost", port = "8080", username = "default" + new Random().nextInt(100);

        try {
            fis = new FileInputStream("src/main/resources/application.properties");
            property.load(fis);

            host = property.getProperty("host");
            port = property.getProperty("port");
            username = property.getProperty("username");

        } catch (IOException e) {
            System.err.println("ОШИБКА: Файл свойств отсуствует!");
        }

        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new

                WebSocketTransport(simpleWebSocketClient));

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new

                MappingJackson2MessageConverter());

        String url = "http://" + host + ":" + port + "/ws";

        StompSessionHandler sessionHandler = new MyStompSessionHandler(username);
        StompSession session = stompClient.connect(url, sessionHandler).get();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print(username + " >> ");
            System.out.flush();
            String line = in.readLine();
            if (line == null || line.equals(".exit")) {
                session.send("/app/chat.sendMessage", new Message(MessageType.LEAVE, "", username));
                break;
            }

            if (line.length() == 0)
                continue;

            session.send("/app/chat.sendMessage", new Message(MessageType.CHAT, line, username));
        }
    }
}
