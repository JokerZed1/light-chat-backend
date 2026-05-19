package com.yougame.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yougame.entity.Game;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GameMapper extends BaseMapper<Game> {

    @Select("SELECT * FROM game ORDER BY sort_order ASC")
    List<Game> selectAll();
}
