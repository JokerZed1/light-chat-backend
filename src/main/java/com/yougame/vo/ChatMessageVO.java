package com.yougame.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor   //无参构造
@AllArgsConstructor  //全参构造
public class ChatMessageVO {

    private String type;       // 消息类型：CHAT / SYSTEM / ONLINE_COUNT
    private Long userId;       // 发送者ID
    private String username;   // 用户名
    private String nickname;   // 昵称
    private String avatar;     // 头像
    private String content;    // 消息内容
    private Long timestamp;    // 时间戳（毫秒）
}