package com.finall.cmt.controller;

import com.finall.cmt.entity.Fans;
import com.finall.cmt.entity.User;
import com.finall.cmt.redis.FansKey;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.service.FansService;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.CodeMsg;
import com.finall.cmt.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Controller
public class FansController {

    private static final Logger log = LoggerFactory.getLogger(FansController.class);

    @Autowired
    FansService fansService;

    @Autowired
    JedisService jedisService;

    @Autowired
    LoginController loginController;

    @Autowired
    UserService userService;

    @GetMapping("/fans/list")
    @ResponseBody
    public Result<List<User>> fansList(HttpServletRequest request) {

        User user = loginController.getUserInfo(request);

        if (user == null) {
            return Result.error(CodeMsg.ERROR);
        } else {
            String userId = user.getUserId();
            String realKey = FansKey.fansKey.getPrefix() + userId;
            Set<String> set = jedisService.smembers(realKey);
            List<User> usersList = new ArrayList<>();
            if (!set.isEmpty()) {
                // 这个set里面全部存储的userId,注意是String类型,然后根据这个来查询出User的信息
                for (String str : set) {
                    User u = userService.selectByUserId(str);
                    usersList.add(u);
                }
                log.info("从Redis中获取我的粉丝列表");
            } else {
                // 如果从Redis拿不到数据的话，就要从mysql中取数据
                List<Fans> fansList = fansService.selectAllFansByUserId(userId);
                for (Fans fans : fansList) {
                    User u = userService.selectByUserId(fans.getFansId());
                    usersList.add(u);
                }
                log.info("从mysql中获取粉丝列表");
            }
            return Result.success(usersList);
        }
    }
}
