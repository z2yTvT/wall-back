package com.finall.cmt.controller;

import com.finall.cmt.entity.User;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.redis.UserTokenKey;
import com.finall.cmt.redis.VerifyCodeKey;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.*;
import com.finall.cmt.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
public class LoginController {

    private Logger log = LoggerFactory.getLogger(RegisterController.class);


    @Autowired
    JedisService jedisService;

    @Autowired
    UserService userService;

    public static final String USER_TOKEN = "token";


    @GetMapping("/loginPassword")
    public Result<UserVo> loginPassword(HttpServletResponse response, String userId, String password) {
        User user = userService.selectByUserId(userId);

        if (user == null) {
            return Result.error(CodeMsg.UNREGISTER);
        }

        if (!(user.getPassword().equals(MD5Utils.md5(password + user.getSalt())))) {
            return Result.error(CodeMsg.PASSWORD_ERROR);
        } else {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            addCookie(response, user);
            return Result.success(userVo);
        }
    }

    public void addCookie(HttpServletResponse response, User user) {
        String token = CommonUtils.uuid();
        log.info("token=" + token);
        // 将token值以及user保存进redis
        jedisService.setKey(UserTokenKey.userTokenKey, token, user);
        Cookie cookie = new Cookie(USER_TOKEN, token);
        // 设置60天的有效期
        cookie.setMaxAge(UserTokenKey.userTokenKey.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);

    }

    public String getUserToken(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }
        return null;
    }


    public User getUserInfo(HttpServletRequest request) {
        String token = getUserToken(request, LoginController.USER_TOKEN);
        return jedisService.getKey(UserTokenKey.userTokenKey, token, User.class);
    }

    /**
     * 当用户点击退出的时候，应该退出登录，并且应该清除Cookie
     */
    @GetMapping("/logout")
    @ResponseBody
    public Result<Boolean> logout(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(LoginController.USER_TOKEN)) {
                // 马上设置该cookie过期
                String token = cookie.getValue();
                cookie.setMaxAge(0);
                cookie.setPath("/");
                // 设置完cookie过期之后，也应该清空Redis保存的Cookie值
                jedisService.del(UserTokenKey.userTokenKey, token);

            }
        }
        return Result.success(true);
    }
}
