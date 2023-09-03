package com.finall.cmt.controller;

import com.finall.cmt.entity.User;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class LeaderBoardController {

    @Autowired
    UserService userService;


    /**
     * 返回成就值排行
     *
     * @return
     */
    @GetMapping("/TopAchieve")
    @ResponseBody
    public Result<List<User>> Top10LeaderBoard() {
        return Result.success(userService.top10LeaderBoard());
    }
}
