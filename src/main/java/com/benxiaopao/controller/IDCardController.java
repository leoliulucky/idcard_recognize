package com.benxiaopao.controller;

import com.benxiaopao.common.util.BASE64Image;
import com.benxiaopao.common.util.FileUtil;
import com.benxiaopao.service.IDCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Api(value="身份证识别模块")
@RestController
public class IDCardController {
    @Autowired
    private IDCardService idCardService;

//    @RequestMapping("/idcard")
//    public Map<String, String> index(){
//        //获取本地的绝对路径图片
//        File file = new File("/Users/leoliu/benxiaopao/我交大/课程学习/软件服务工程/作业准备/项目/myself.jpeg");
//        //进行BASE64位编码
//        String imageBase = BASE64Image.encodeImgageToBase64(file);
//        imageBase = imageBase.replaceAll("\r\n","");
//        imageBase = imageBase.replaceAll("\\+","%2B");
//        Map<String, String> idCardInfo = idCardService.getIDCardInfo(imageBase);
//
//        return idCardInfo;
//    }

    /**
     * 识别身份证正面照片文字，返回JSON格式
     * @param file
     * @return
     */
    @ApiOperation(value = "识别身份证正面照片文字", notes = "参数file为身份证正面图片文件")
    @RequestMapping(value = "/idcard", method = RequestMethod.POST)
    public Map<String, Object> idcard(@ApiParam(value = "身份证正面图片文件", required = true) @RequestParam("file") MultipartFile file){
        Map<String, Object> result = new HashMap<String, Object>();
        int code = 500;
        String msg = "上传失败！";

        if(file == null){
            msg = "接口参数file为空，请检查并重试！";
            result.put("code", code);
            result.put("msg", msg);
            return result;
        }

        String path = this.getClass().getResource("/").getPath() + "upload";
        System.out.println("######path=" + path);
        if (FileUtil.upload(file, path, file.getOriginalFilename())){
            // 上传成功，给出页面提示
            code = 200;
            msg = "上传成功！";
        }

        // 显示图片
        result.put("msg", msg);
//        map.put("fileName", file.getOriginalFilename());

        //获取本地的绝对路径图片
        File localFile = new File(path + "/" + file.getOriginalFilename());

        //进行BASE64位编码
        String imageBase = BASE64Image.encodeImgageToBase64(localFile);
        imageBase = imageBase.replaceAll("\r\n","");
        imageBase = imageBase.replaceAll("\\+","%2B");
        Map<String, String> idCardInfo = idCardService.getIDCardInfo(imageBase);

        result.put("code", code);
        result.put("data", idCardInfo);
        return result;
    }

}
