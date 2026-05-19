package com.yougame.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yougame.entity.User;
import com.yougame.mapper.UserMapper;
import com.yougame.vo.ChatMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 在线用户：userId -> WebSocketSession
    private static final Map<Long, WebSocketSession> ONLINE_SESSIONS = new ConcurrentHashMap<>();
    private static final String REDIS_KEY = "chat:messages";
    private static final int MAX_MESSAGES = 200;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            session.close();
            return;
        }

        ONLINE_SESSIONS.put(userId, session);
        log.info("用户 {} 加入聊天室，当前在线人数：{}", userId, ONLINE_SESSIONS.size());

        // 发送历史消息
        sendHistoryMessages(session);

        // 广播系统消息
        User user = userMapper.selectById(userId);
        String nickname = user != null ? user.getNickname() : "未知用户";
        ChatMessageVO systemMsg = ChatMessageVO.builder()
                .type("SYSTEM")
                .content(nickname + " 加入了聊天室")
                .timestamp(System.currentTimeMillis())
                .build();
        broadcast(systemMsg);

        // 广播在线人数
        broadcastOnlineCount();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) return;

        // 解析前端发来的消息
        String payload = message.getPayload();
        ChatMessageVO chatMessage;
        try {
            chatMessage = objectMapper.readValue(payload, ChatMessageVO.class);
        } catch (JsonProcessingException e) {
            log.error("消息格式错误：{}", payload);
            return;
        }

        // 填充用户信息
        User user = userMapper.selectById(userId);
        if (user != null) {
            chatMessage.setUserId(userId);
            chatMessage.setUsername(user.getUsername());
            chatMessage.setNickname(user.getNickname());
            chatMessage.setAvatar(user.getAvatar());
        }
        chatMessage.setType("CHAT");
        chatMessage.setTimestamp(System.currentTimeMillis());

        // 存储到 Redis
        saveToRedis(chatMessage);

        // 广播给所有人
        broadcast(chatMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            ONLINE_SESSIONS.remove(userId);
            log.info("用户 {} 离开聊天室，当前在线人数：{}", userId, ONLINE_SESSIONS.size());

            User user = userMapper.selectById(userId);
            String nickname = user != null ? user.getNickname() : "未知用户";
            ChatMessageVO systemMsg = ChatMessageVO.builder()
                    .type("SYSTEM")
                    .content(nickname + " 离开了聊天室")
                    .timestamp(System.currentTimeMillis())
                    .build();
            broadcast(systemMsg);
            broadcastOnlineCount();
        }
    }

    // ==================== 私有工具方法 ====================

    private void broadcast(ChatMessageVO message) {
        String json;
        try {
            json = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("消息序列化失败", e);
            return;
        }
        TextMessage textMessage = new TextMessage(json);
        for (WebSocketSession session : ONLINE_SESSIONS.values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(textMessage);
                } catch (IOException e) {
                    log.error("发送消息失败，sessionId：{}", session.getId());
                }
            }
        }
    }

    private void saveToRedis(ChatMessageVO message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().leftPush(REDIS_KEY, json);
            redisTemplate.opsForList().trim(REDIS_KEY, 0, MAX_MESSAGES - 1);
        } catch (JsonProcessingException e) {
            log.error("存储消息到 Redis 失败", e);
        }
    }

    private void sendHistoryMessages(WebSocketSession session) {
        try {
            var messages = redisTemplate.opsForList().range(REDIS_KEY, 0, MAX_MESSAGES - 1);
            if (messages != null && !messages.isEmpty()) {
                // 因为 leftPush 是逆序的，需要反转顺序使时间正序
                for (int i = messages.size() - 1; i >= 0; i--) {
                    ChatMessageVO msg = objectMapper.readValue(messages.get(i), ChatMessageVO.class);
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(msg)));
                }
            }
        } catch (Exception e) {
            log.error("发送历史消息失败", e);
        }
    }

    private void broadcastOnlineCount() {
        ChatMessageVO countMsg = ChatMessageVO.builder()
                .type("ONLINE_COUNT")
                .content(String.valueOf(ONLINE_SESSIONS.size()))
                .timestamp(System.currentTimeMillis())
                .build();
        broadcast(countMsg);
    }
}