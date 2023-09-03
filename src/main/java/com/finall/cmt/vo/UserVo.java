package com.finall.cmt.vo;

import lombok.Data;

import java.util.Date;


@Data
public class UserVo {

    /**
     * 这里可以看做是手机号
     */
    private String userId;

    private String nickname;

    private String avatar;

    private int achieveValue;

    private String school;

    /**
     * 添加性别属性 1表示male,0表示female
     */
    private int sex;

    /**
     * 添加个性签名的属性
     */
    private String signature;
}
