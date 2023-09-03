package com.finall.cmt.controller;

import com.finall.cmt.entity.Notice;
import com.finall.cmt.entity.User;
import com.finall.cmt.service.NoticeService;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.CodeMsg;
import com.finall.cmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class NoticeController {

    @Autowired
    LoginController loginController;

    @Autowired
    NoticeService noticeService;

    @Autowired
    UserService userService;


    @GetMapping("/hasReadNotice")
    @ResponseBody
    public Result<Boolean> hasReadIsZero(HttpServletRequest request) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            return Result.error(CodeMsg.NOT_LOGIN);
        }

        int count = noticeService.countNoticeHasRead(user.getUserId());
        if (count > 0) {
            return Result.success(true);
        } else {
            return Result.success(false);
        }
    }

    @GetMapping("/notice/list")
    @ResponseBody
    public Result<List<Notice>> noticeList(HttpServletRequest request) {
        User user = loginController.getUserInfo(request);
        if (user == null) {
            return Result.error(CodeMsg.NOT_LOGIN);
        }
        noticeService.updateAllNoticeHasRead(user.getUserId());
        List<Notice> notices = noticeService.noticeList(user.getUserId());
        return Result.success(notices);
    }
}
