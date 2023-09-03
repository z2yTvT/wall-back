package com.finall.cmt.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.finall.cmt.dao.FileCollectDao;
import com.finall.cmt.dao.FileDetailDao;
import com.finall.cmt.entity.FileCollect;
import com.finall.cmt.entity.FileDetail;
import com.finall.cmt.entity.req.FileCollectDetailReq;
import com.finall.cmt.entity.req.FileCollectReq;
import com.finall.cmt.utils.CodeMsg;
import com.finall.cmt.utils.RandomUtils;
import com.finall.cmt.utils.Result;
import com.google.gson.Gson;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(value = "/tools")
public class ToolsController {
    private static Logger log = LoggerFactory.getLogger(ToolsController.class);
    @Value("${qiniuyun.AccessKey}")
    private String accessKey;

    @Value("${qiniuyun.SecretKey}")
    private String secretKey;

    @Value("${qiniuyun.Bucket}")
    private String bucket;

    @Value("${qiniuyun.domain.name}")
    private String domainName;

    @Autowired
    private FileCollectDao fileCollectDao;
    @Autowired
    private FileDetailDao fileDetailDao;

    @PostMapping("/getCollectFileList")
    public Result<List<FileDetail>> getCollectFileList(@RequestBody FileCollectReq req){
        String collectNo = req.getCollectNo();
        if(StringUtils.isBlank(collectNo)){
            return Result.error(CodeMsg.PARAMETER_ERROR);
        }
        LambdaQueryWrapper<FileDetail> ldw = Wrappers.lambdaQuery();
        ldw.eq(FileDetail::getCollectNo,collectNo)
                .eq(FileDetail::getDeleted,0);
        List<FileDetail> fileDetails = fileDetailDao.selectList(ldw);
        return Result.success(fileDetails);
    }

    @PostMapping("/getFileCollect")
    public Result<FileCollect> getFileCollect(@RequestBody FileCollectReq req){
        String collectNo = req.getCollectNo();
        if(StringUtils.isBlank(collectNo)){
            return Result.error(CodeMsg.PARAMETER_ERROR);
        }
        LambdaQueryWrapper<FileCollect> ldw = Wrappers.lambdaQuery();
        ldw.eq(FileCollect::getCollectNo,collectNo)
                .eq(FileCollect::getDeleted,0);
        FileCollect fileCollect = fileCollectDao.selectOne(ldw);
        return Result.success(fileCollect);
    }

    @PostMapping("/checkExist")
    public Result<Boolean> checkExist(@RequestBody FileCollectReq req){
        String collectNo = req.getCollectNo();
        if(StringUtils.isBlank(collectNo)){
            return Result.success(Boolean.FALSE);
        }
        LambdaQueryWrapper<FileCollect> ldw = Wrappers.lambdaQuery();
        ldw.eq(FileCollect::getCollectNo,collectNo);
        Integer count = fileCollectDao.selectCount(ldw);
        return Result.success(count > 0);
    }


    @PostMapping("/saveCollectFileMsg")
    public Result<CodeMsg> saveCollectFileMsg(@RequestBody FileCollectDetailReq req) {
        FileDetail fileDetail = new FileDetail();
        fileDetail.setFileUrl(req.getFileUrl());
        fileDetail.setCollectNo(req.getCollectNo());
        fileDetail.setFileName(req.getFileDesc());
        fileDetail.setFileSuffix(req.getFileSuffix());
        fileDetail.setFileDesc(req.getFileDesc());
        fileDetail.setCreateTime(new Date());
        fileDetail.setUpdateTime(new Date());

        fileDetailDao.insert(fileDetail);
        return Result.success(CodeMsg.SUCCESS);
    }

    @PostMapping("/createFileCollect")
    @Transactional
    public Result<CodeMsg> createFileCollect(@RequestBody FileCollectReq req) {
        if (StringUtils.isBlank(req.getCollectNo()) || StringUtils.isBlank(req.getTittle())) {
            return Result.error(CodeMsg.COLLECT_PARAMETER_ERROR);
        }
        LambdaQueryWrapper<FileCollect> ldw = Wrappers.lambdaQuery();
        ldw.eq(FileCollect::getCollectNo, req.getCollectNo());
        Integer cnt = fileCollectDao.selectCount(ldw);
        if (cnt > 0) {
            return Result.error(CodeMsg.COLLECTNO_EXIST);
        }
        FileCollect fileCollect = new FileCollect();
        fileCollect.setCollectNo(req.getCollectNo());
        fileCollect.setTittle(req.getTittle());
        fileCollect.setCreateTime(new Date());
        fileCollect.setUpdateTime(new Date());
        fileCollectDao.insert(fileCollect);
        return Result.success(CodeMsg.SUCCESS);

    }

    @PostMapping("/uploadFiles")
    @ResponseBody
    public Result<String> uploadImages2Qiniuyun(MultipartFile file) {
        // 默认是是将图片的存放的地址设为了华南地区
        Configuration cfg = new Configuration(Region.huanan());
        UploadManager uploadManager = new UploadManager(cfg);

        // 默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = RandomUtils.randomSalt();
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        System.out.println(upToken);
        try {
            if (Objects.isNull(file)) {
                return Result.error(CodeMsg.UPLOAD_IMAGE_EMPTY);
            }

            Response response = uploadManager.put(file.getBytes(), key, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            // 拿到这个Key之后，然后拼接域名 + "/" + key，就是该图片的路径了
            String result = domainName + "/" + putRet.key;
            // 返回的就是图片的url地址了
            log.info("图片的存储地址是：{}", result);
            return Result.success(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/downLoad")
    public void download(@RequestParam("collectNo") String collectNo, HttpServletResponse response) {
        log.info(collectNo + "===");
        LambdaQueryWrapper<FileDetail> ldw = Wrappers.lambdaQuery();
        ldw.eq(FileDetail::getCollectNo, collectNo)
                .eq(FileDetail::getDeleted, 0);
        List<FileDetail> details = fileDetailDao.selectList(ldw);
        LambdaQueryWrapper<FileCollect> ldw2 = Wrappers.lambdaQuery();
        ldw2.eq(FileCollect::getCollectNo, collectNo)
                .eq(FileCollect::getDeleted, 0);
        FileCollect fileCollect = fileCollectDao.selectOne(ldw2);

        if (details == null) return;
        try {
            String downloadFilename = fileCollect.getTittle() + ".zip";//文件的名称
            downloadFilename = URLEncoder.encode(downloadFilename, "UTF-8");//转换中文否则可能会产生乱码
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFilename);// 设置在下载框默认显示的文件名
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            for (FileDetail detail : details) {
                URL url = new URL("http://" + detail.getFileUrl());
                zos.putNextEntry(new ZipEntry(detail.getFileName() + "." + detail.getFileSuffix()));
                InputStream fis = url.openConnection().getInputStream();
                byte[] buffer = new byte[1024];
                int r = 0;
                while ((r = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, r);
                }
                fis.close();
            }
            zos.flush();
            zos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
