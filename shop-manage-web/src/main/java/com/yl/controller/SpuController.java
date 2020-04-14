package com.yl.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yl.utils.FastDFSClient;
import com.yl.bean.FastDFSFile;
import com.yl.bean.PmsProductInfo;
import com.yl.service.SpuService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@CrossOrigin
public class SpuController {

    @Reference
    SpuService spuService;

    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) {
        String path = null;
        // 将图片或者音视频上传到分布式的文件存储系统
        if (multipartFile.isEmpty()) {
            System.out.println("Please select a file to upload");
        }
        try {
            // Get the file and save it somewhere
            path = saveFile(multipartFile);
            System.out.println("You successfully uploaded '" + multipartFile.getOriginalFilename() + "'..." + "file path url '" + path + "'");
        } catch (Exception e) {
            System.out.println("upload file failed");
        }

        // 将图片的存储路径返回给页面
        return path;
    }

    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo) {

        return "success";
    }

    @RequestMapping("spuList")
    public List<PmsProductInfo> spuList(String catalog3Id) {

        List<PmsProductInfo> pmsProductInfos = spuService.spuList(catalog3Id);

        return pmsProductInfos;
    }

    private String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};

        // 获得文件后缀名
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);

        // 获得上传的二进制对象
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();

        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();

        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);

        try {
            fileAbsolutePath = FastDFSClient.upload(file);  //upload to fastdfs
        } catch (Exception e) {
            System.out.println("upload file Exception!");
        }

        if (fileAbsolutePath == null) {
            System.out.println("upload file failed,please upload again!");
        }

        String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        return path;
    }
}
