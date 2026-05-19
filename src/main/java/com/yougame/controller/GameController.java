package com.yougame.controller;


import com.yougame.common.result.Result;
import com.yougame.entity.Game;
import com.yougame.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/games")
public class GameController {


    private final GameMapper gameMapper;

    @GetMapping
    public Result<List<Game>> getAllGame() {
        return Result.success(gameMapper.selectAll());
    }
}
