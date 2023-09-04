package com.atguigu.crowd.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.aliyun.api.gateway.demo.util.HttpUtils;
import com.aliyun.oss.*;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.PutObjectResult;
import com.atguigu.crowd.constant.CrowdConstant;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import java.util.Date;
import java.util.UUID;


/**
 * 尚筹网项目通用工具方法类
 * @author 吴彦祖
 *
 */
public class CrowdUtil {

	
	/**
	 * 对明文字符串进行MD5加密
	 * @param source 传入的明文字符串
	 * @return 加密结果
	 */
	public static String md5(String source) {
		
		// 1.判断source是否有效
		if(source == null || source.length() == 0) {
		
			// 2.如果不是有效的字符串抛出异常
			throw new RuntimeException(CrowdConstant.MESSAGE_STRING_INVALIDATE);
		}
		
		try {
			// 3.获取MessageDigest对象
			String algorithm = "md5";
			
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			
			// 4.获取明文字符串对应的字节数组
			byte[] input = source.getBytes();
			
			// 5.执行加密
			byte[] output = messageDigest.digest(input);
			
			// 6.创建BigInteger对象
			int signum = 1;
			BigInteger bigInteger = new BigInteger(signum, output);
			
			// 7.按照16进制将bigInteger的值转换为字符串
			int radix = 16;
			String encoded = bigInteger.toString(radix).toUpperCase();
			
			return encoded;
		
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 判断当前请求是否为Ajax请求
	 * @param request 请求对象
	 * @return
	 * 		true：当前请求是Ajax请求
	 * 		false：当前请求不是Ajax请求
	 */
	public static boolean judgeRequestType(HttpServletRequest request) {
		
		// 1.获取请求消息头
		String acceptHeader = request.getHeader("Accept");
		String xRequestHeader = request.getHeader("X-Requested-With");
		
		// 2.判断
		return (acceptHeader != null && acceptHeader.contains("application/json"))
				
				||
				
				(xRequestHeader != null && xRequestHeader.equals("XMLHttpRequest"));
	}

	/**
	 * 发送短信验证码
	 * @param host 短信接口调用的URL地址
	 * @param path 具体发送短信功能的地址
	 * @param method 请求方式
	 * @param appcode 用来调用第三方短信的API的AppCode
	 * @param mobile 接受短信的手机号
	 * @param templateId 短信模板
	 * @param smsSignId 短信签名(前缀)
	 * @return
	 */
	public static ResultEntity<String> sendCodeByShortMessage(String host,
															  String path,
															  String method,
															  String appcode,
															  String mobile,
															  String templateId,
															  String smsSignId) {
//		String host = "https://gyytz.market.alicloudapi.com";
//		String path = "/sms/smsSend";
//		String method = "POST";
//		String appcode = "7b570678bdaf43a0ad6ac0297f1cad20";
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < 4; i++){
			int random = (int)(Math.random() * 10);
			builder.append(random);
		}
		String code = builder.toString();
		Map<String, String> headers = new HashMap<String, String>();
		//最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
		headers.put("Authorization", "APPCODE " + appcode);
		Map<String, String> querys = new HashMap<String, String>();
		querys.put("mobile", mobile);
		querys.put("param", "**code**:"+ code + ",**minute**:5");

//smsSignId（短信前缀）和templateId（短信模板），可登录国阳云控制台自助申请。参考文档：http://help.guoyangyun.com/Problem/Qm.html

//		querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
//		querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
		querys.put("smsSignId", smsSignId);
		querys.put("templateId", templateId);
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
			int statusCode = statusLine.getStatusCode();
			String reasonPhrase = statusLine.getReasonPhrase();
			// 状态码: 200 正常；400 URL无效；401 appCode错误； 403 次数用完； 500 API网管错误
			if (statusCode == 200) {
				return ResultEntity.successWithData(code);
			}
			return ResultEntity.failed(reasonPhrase);
			// System.out.println(response.toString());如不输出json, 请打开这行代码，打印调试头部状态码。
			//获取response的body
		} catch (Exception e) {
			e.printStackTrace();
			return ResultEntity.failed(e.getMessage());
		}
	}
	/**
	 * 短信服务不可用时的模拟方法
	 * @param smsSignId 签名，随便填
	 * @param templateId 模板编号，随便填
	 * @param mobile 手机号码
	 * @return 返回生成的随机验证码
	 */
	public static ResultEntity<String> sendMessageSimulation(String templateId,
														 String smsSignId,
														 String mobile){

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < 4; i++){
			int random = (int)(Math.random() * 10);
			builder.append(random);
		}
		String code = builder.toString();
		return ResultEntity.successWithData(code);
	}

	public static ResultEntity<String> uploadFileToOss(String endpoint, String accessKeyId, String accessKeySecret, InputStream inputStream, String bucketName, String bucketDomain, String originalName) {
		// 创建OSSClient实例。
		OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

		// 生成上传文件的目录
		String folderName = new SimpleDateFormat("yyyyMMdd").format(new Date());

		// 生成上传文件在OSS服务器上保存时的文件名
		// 原始文件名：beautfulgirl.jpg
		// 生成文件名：wer234234efwer235346457dfswet346235.jpg
		// 使用UUID生成文件主体名称
		String fileMainName = UUID.randomUUID().toString().replace("-", "");

		// 从原始文件名中获取文件扩展名
		String extensionName = originalName.substring(originalName.lastIndexOf("."));

		// 使用目录、文件主体名称、文件扩展名称拼接得到对象名称
		String objectName = folderName + "/" + fileMainName + extensionName;

		try {
			// 调用OSS客户端对象的方法上传文件并获取响应结果数据
			PutObjectResult putObjectResult = ossClient.putObject(bucketName, objectName, inputStream);
			// 从响应结果中获取具体响应消息
			ResponseMessage responseMessage = putObjectResult.getResponse();

			// 根据响应状态码判断请求是否成功
			if(responseMessage == null){
				// 拼接访问刚刚上传的文件的路径
				String ossFileAccessPath = bucketDomain + "/" + objectName;

				// 当前方法返回成功
				return ResultEntity.successWithData(ossFileAccessPath);
			}else{
				// 获取响应状态码
				int statusCode = responseMessage.getStatusCode();
				// 如果请求没有成功，获取错误消息
				String errorMessage = responseMessage.getErrorResponseAsString();
				// 当前方法返回失败
				return ResultEntity.failed("当前响应状态码=" + statusCode + " 错误消息=" + errorMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 当前方法返回失败
			return ResultEntity.failed(e.getMessage());
		} finally {
			if(ossClient != null){
				// 关闭OSSClient。
				ossClient.shutdown();
			}
		}
	}

}
