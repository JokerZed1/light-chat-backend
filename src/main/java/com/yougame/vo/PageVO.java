// 分页结果 VO

package com.yougame.vo;


import lombok.Data;

import java.util.List;

@Data
public class PageVO<T> {
    private List<T> records;    // 当前页数据
    private Long total;         // 总记录数
    private Integer page;       // 当前页码
    private Integer size;       // 每页数量
    private Integer pages;      // 总页数

    public PageVO(List<T> records, Long total, Integer page, Integer size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }
}
