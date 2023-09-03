package com.finall.cmt.utils;

import lombok.Data;


@Data
public class CodeMsg {

    private int code;
    private String msg;

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 通用错误
     */
    public static CodeMsg SUCCESS = new CodeMsg(0, "SUCCESS");
    public static CodeMsg ERROR = new CodeMsg(000000, "ERROR");
    public static CodeMsg NOT_LOGIN = new CodeMsg(000001, "NOT_LOGIN");

    /**
     * 登录模块错误
     */
    public static CodeMsg VERIFY_CODE_ERROR = new CodeMsg(100000, "验证码错误");
    public static CodeMsg DUPLICATE_REGISTRY = new CodeMsg(100001, "账号重复注册");
    public static CodeMsg UNREGISTER = new CodeMsg(100002, "未注册");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(100003, "登录密码错误");
    public static CodeMsg PARAMETER_ERROR = new CodeMsg(100007, "参数有误");
    public static CodeMsg USER_EXIST = new CodeMsg(100008, "用户ID已被注册");


    /**
     * 个人中心模块错误
     */
    public static CodeMsg UPLOAD_IMAGE_EMPTY = new CodeMsg(200000, "上传图片为空");

    /**
     * 文件上传模块错误
     */
    public static CodeMsg COLLECTNO_EXIST = new CodeMsg(300000, "上传序列号已被存在");
    public static CodeMsg COLLECT_PARAMETER_ERROR = new CodeMsg(300001, "文件收集参数有误");

}
