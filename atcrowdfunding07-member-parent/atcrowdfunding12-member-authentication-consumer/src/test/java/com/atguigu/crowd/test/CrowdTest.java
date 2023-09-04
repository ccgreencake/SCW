package com.atguigu.crowd.test;

import com.aliyun.api.gateway.demo.util.HttpUtils;
import com.atguigu.crowd.util.ResultEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static com.atguigu.crowd.util.CrowdUtil.uploadFileToOss;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/7/15 20:33
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CrowdTest {
    private Logger logger = LoggerFactory.getLogger(CrowdTest.class);
    @Test
    public void testSendMessage(){
        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "7b570678bdaf43a0ad6ac0297f1cad20";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "15907675340");
        querys.put("param", "**code**:1234,**minute**:5");

//smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html

        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从\r\n\t    \t* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java\r\n\t    \t* 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            StatusLine statusLine = response.getStatusLine();

            // 状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
            int statusCode = statusLine.getStatusCode();
            logger.info("code="+statusCode);

            String reasonPhrase = statusLine.getReasonPhrase();
            logger.info("reason="+reasonPhrase);

            // System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
            //获取response的body
            logger.info(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testCode(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 4; i++){
            int random = (int)(Math.random() * 10);
            builder.append(random);
        }
        String code = builder.toString();
        logger.info("code="+code);
    }
    @Test
    public void testOSS() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("/Users/dobi/Desktop/WechatIMG468.jpg");
        ResultEntity<String> stringResultEntity = uploadFileToOss("oss-cn-shenzhen.aliyuncs.com", "LTAI5tGHWEYcx73YTGHGjPYh", "M4hC9XPg09QhCMejaY0uGZrvIy6vPU", fileInputStream, "ldbscw001", "ldbscw001.oss-cn-shenzhen.aliyuncs.com", "WechatIMG468.jpg");
        System.out.println(stringResultEntity);
    }

}
