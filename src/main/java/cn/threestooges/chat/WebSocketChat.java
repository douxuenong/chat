package cn.threestooges.chat;

import cn.threestooges.chat.domain.Message;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天服务器
 */
@Component
@ServerEndpoint("/chat")
public class WebSocketChat {
    //存储在线会话
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    /**
     * 当客户端打开连接：1.添加会话对象 2.更新在线人数
     */
    @OnOpen
    public void onOpen(Session session) {
        onlineSessions.put(session.getId(), session);
        session.getAsyncRemote().sendText("当前在线人数："+onlineSessions.size());
    }

    /**
     * 关闭时删去该用户
     */
    @OnClose
    public void onClose(Session session){
        onlineSessions.remove(session.getId());
        session.getAsyncRemote().sendText("有用户退出聊天，当前在线人数："+onlineSessions.size());
    }

    /**
     * 当客户端发送消息：1.获取它的用户名和消息 2.发送消息给所有人
     * <p>
     * PS: 这里约定传递的消息为JSON字符串 方便传递更多参数！
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        Message message = JSON.parseObject(jsonStr, Message.class);
        sendMessageToAll(message.getMsg());
    }

    /**
     * 公共方法：发送信息给所有人
     */
    private static void sendMessageToAll(String msg) {
        onlineSessions.forEach((id, session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
