package com.benxiaopao.service.impl;

import com.benxiaopao.common.util.BaiduOcr;
import com.benxiaopao.service.IDCardService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class IDCardServiceImpl implements IDCardService {
    private static String BAIDU_URL =  "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";

    @Override
    public Map<String, String> getIDCardInfo(String imageData) {
        String result = invokeBaidu(imageData);
        System.out.println("返回的结果：" + result);
        Map<String, String> idCardInfo = getIDCardInfoFromJson(result);
        return idCardInfo;
    }

    public String invokeBaidu(String imageData) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        try {
            String accessToken = BaiduOcr.getAuth();
            String httpUrl = buildHttUrl(accessToken);
            String httpArg = buildHttArg(imageData);
            //用java JDK自带的URL去请求
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //设置该请求的消息头
            //设置HTTP方法：POST
            connection.setRequestMethod("POST");
            //设置其Header的Content-Type参数为application/x-www-form-urlencoded
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey", BaiduOcr.getApiKey());
            //将第二步获取到的token填入到HTTP header
            connection.setRequestProperty("access_token", accessToken);
            connection.setDoOutput(true);
            connection.getOutputStream().write(httpArg.getBytes("UTF-8"));
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String buildHttUrl(String accessToken){
        String httpUrl = BAIDU_URL + "?access_token=" + accessToken;
        return httpUrl;
    }

    private String buildHttArg(String imageData){
        String httpArg = "detect_direction=true&id_card_side=front&image=" + imageData;
        return httpArg;
    }

    private Map<String, String> getIDCardInfoFromJson(String jsonResult){
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            JSONObject wordsResult= jsonObject.getJSONObject("words_result");
            Iterator<String> it = wordsResult.keys();
            while (it.hasNext()){
                String key = it.next();
                JSONObject result = wordsResult.getJSONObject(key);
                String value=result.getString("words");
                switch (key){
                    case "姓名":
//                        map.put("name",value);
                        map.put(key, value);
                        break;
                    case "民族":
//                        map.put("nation",value);
                        map.put(key, value);
                        break;
                    case "住址":
//                        map.put("address",value);
                        map.put(key, value);
                        break;
                    case "公民身份号码":
//                        map.put("IDCard",value);
                        map.put(key, value);
                        break;
                    case "出生":
//                        map.put("Birth",value);
                        map.put(key, value);
                        break;
                    case "性别":
//                        map.put("sex",value);
                        map.put(key, value);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
