package com.damien.webSocket;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    /** 存储所有在线用户的会话 */
    private static Map<String, Session> sessionMap = new HashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("有新的连接，sid为：{}", sid);
        sessionMap.put(sid, session);
    }

    /**
     * 收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid){
        log.info("收到客户端消息，sid为：{}，消息为：{}", sid, message);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid){
        log.info("连接关闭，sid为：{}", sid);
        sessionMap.remove(sid);
    }

    /**
     * 服务端给指定用户发送消息
     */
    public void sendMessageToClient(String message, String sid) {
        log.info("服务端给客户端[{}]发送消息{}", sid, message);
        Session session = sessionMap.get(sid);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 服务端给所有用户发送消息
     */
    public void sendMessageToAllClient(String message) {
        log.info("服务端给所有用户发送消息{}", message);
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions){
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("服务端给所有用户发送消息异常", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 构建消息
     */
    public String buildMessage(HashMap<String, Object> params) {
        return JSONObject.toJSONString(params);
    }


}
