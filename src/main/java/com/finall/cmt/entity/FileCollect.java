package com.finall.cmt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

/**
 * 工具箱文件上传细节表
 * @TableName file_collect
 */
@Data
public class FileCollect{
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 收集序列号
     */
    private String collectNo;

    /**
     * 标题
     */
    private String tittle;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新时间
     */
    @TableLogic
    private Integer deleted;
}