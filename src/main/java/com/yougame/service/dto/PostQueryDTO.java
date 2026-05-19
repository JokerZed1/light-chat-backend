package com.yougame.service.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PostQueryDTO {
    private Integer categoryId;
    private String section = "ALL";   // 默认值
    private Integer gameId;        // 新增

    // 排序字段，只允许这三个值
    private String orderBy = "create_time";

    @Min(value = 1, message = "页码最小为1")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量最小为1")
    private Integer size = 10;

    // 白名单校验方法
    public String getValidOrderBy() {
        if (orderBy == null) return "create_time";
        return switch (orderBy) {
            case "like_count", "comment_count", "create_time" -> orderBy;
            default -> "create_time";  // 非法值统一回退到默认排序
        };
    }
}