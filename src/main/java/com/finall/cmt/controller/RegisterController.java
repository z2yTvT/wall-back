package com.finall.cmt.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.finall.cmt.dao.UserDao;
import com.finall.cmt.entity.User;
import com.finall.cmt.redis.JedisService;
import com.finall.cmt.redis.VerifyCodeKey;
import com.finall.cmt.service.UserService;
import com.finall.cmt.utils.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
@RequestMapping(value = "/register")
public class RegisterController {

    private Logger log = LoggerFactory.getLogger(RegisterController.class);


    @Autowired
    private JedisService jedisService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;



    /**
     * 这里的参数传递应该是传递的是校验码,校验验证码，判断用户填的校验码和用Redis临时存储的验证码是否匹配
     *
     * @param code
     * @return
     */
    @GetMapping("/verifyRegisterInfo")
    public Result<CodeMsg> registerVerify(String code, String userId, String password) {
        String verifyCode = jedisService.getKey(VerifyCodeKey.verifyCodeKeyRegister, code, String.class);
        if (verifyCode == null) {
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }

        User u = userService.selectByUserId(userId);
        if (u != null) {
            return Result.error(CodeMsg.DUPLICATE_REGISTRY);
        }
        // 随机生成一个6位数的小写字符串
        String salt = RandomUtils.randomSalt();
        String nickname = "用户" + RandomUtils.randomNickName() + "号";

        User user = new User();
        user.setUserId(userId);
        user.setSalt(salt);
        user.setPassword(MD5Utils.md5(password + salt));
        user.setNickname(nickname);
        user.setLoginIp("phone");
        user.setCreateTime(new Date());
        userService.insert(user);
        return Result.success(CodeMsg.SUCCESS);
    }

    @GetMapping("/regByAccount")
    public Result<CodeMsg> registerByAct(String userId, String password){
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(password)){
            return Result.error(CodeMsg.PARAMETER_ERROR);
        }
        LambdaQueryWrapper<User> ldw = Wrappers.lambdaQuery();
        ldw.eq(User::getUserId,userId);
        Integer cnt = userDao.selectCount(ldw);
        if(cnt > 0){
            return Result.error(CodeMsg.USER_EXIST);
        }
        String salt = RandomUtils.randomSalt();
        String nickname = "用户" + RandomUtils.randomNickName() + "号";

        User user = new User();
        user.setUserId(userId);
        user.setSalt(salt);
        user.setPassword(MD5Utils.md5(password + salt));
        user.setNickname(nickname);
        user.setLoginIp("account");
        user.setCreateTime(new Date());
        userDao.insert(user);
        return Result.success(CodeMsg.SUCCESS);
    }

}
