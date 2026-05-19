// 发布帖子 DTO

package com.yougame.service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostPublishDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "请选择分类")
    private Integer categoryId;

    @NotNull(message = "请选择游戏")
    private Integer gameId;

}
