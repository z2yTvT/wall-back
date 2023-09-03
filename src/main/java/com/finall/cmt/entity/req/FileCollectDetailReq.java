package com.finall.cmt.entity.req;

import lombok.Data;

@Data
public class FileCollectDetailReq {
    private String fileUrl;
    private String fileDesc;
    private String collectNo;
    private String fileSuffix;
}
