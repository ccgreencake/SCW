package com.atguigu.crowd.handler;

import com.atguigu.crowd.api.MySQLRemoteService;
import com.atguigu.crowd.api.RedisRemoteService;
import com.atguigu.crowd.config.ShortMessageProperties;
import com.atguigu.crowd.constant.CrowdConstant;
import com.atguigu.crowd.entity.po.MemberPO;
import com.atguigu.crowd.entity.vo.MemberLoginVO;
import com.atguigu.crowd.entity.vo.MemberVO;
import com.atguigu.crowd.util.CrowdUtil;
import com.atguigu.crowd.util.ResultEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/7/17 10:39
 */
@Controller
public class MemberHandler {
    @Autowired
    private ShortMessageProperties shortMessageProperties;

    @Autowired
    private RedisRemoteService redisRemoteService;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

//    //真正发送短信验证码
//    @ResponseBody
//    @RequestMapping("/auth/member/send/short/message.json")
//    public ResultEntity<String> sendMessage(@RequestParam("phoneNum") String phoneNum) {
//        ResultEntity<String> sendCodeByShortMessage = CrowdUtil.sendCodeByShortMessage(
//                shortMessageProperties.getHost(),
//                shortMessageProperties.getPath(),
//                shortMessageProperties.getMethod(),
//                shortMessageProperties.getAppcode(),
//                phoneNum,
//                shortMessageProperties.getTemplateId(),
//                shortMessageProperties.getSmsSignId());
//        if(ResultEntity.SUCCESS.equals(sendCodeByShortMessage.getResult())) {
//            String data = sendCodeByShortMessage.getData();
//            String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;
//            ResultEntity<String> saveCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, data, 5, TimeUnit.MINUTES);
//            if (ResultEntity.SUCCESS.equals(saveCodeResultEntity.getResult())) {
//                return ResultEntity.successWithoutData();
//            } else {
//                return saveCodeResultEntity;
//            }
//
//        }
//        else {
//            return sendCodeByShortMessage;
//        }
//    }

    // 模拟发送短信验证码(不用真正发送短信)
    @ResponseBody
    @RequestMapping("/auth/member/send/short/message.json")
    public ResultEntity<String> sendMessage(@RequestParam("phoneNum") String phoneNum) {
        ResultEntity<String> sendCodeByShortMessage = CrowdUtil.sendMessageSimulation(
                shortMessageProperties.getTemplateId(),
                shortMessageProperties.getSmsSignId(),
                phoneNum
        );

        if(ResultEntity.SUCCESS.equals(sendCodeByShortMessage.getResult())) {
            String data = sendCodeByShortMessage.getData();
            String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;
            ResultEntity<String> saveCodeResultEntity = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, data, 5, TimeUnit.MINUTES);
            if (ResultEntity.SUCCESS.equals(saveCodeResultEntity.getResult())) {
                return ResultEntity.successWithoutData();
            } else {
                return saveCodeResultEntity;
            }

        }
        else {
            return sendCodeByShortMessage;
        }
    }

    // 注册
    @RequestMapping("/auth/do/member/register")
    public String register(MemberVO memberVO, ModelMap modelMap) {


        // 1.获取用户输入的手机号
        String phoneNum = memberVO.getPhoneNum();

        // 2.拼接Redis中存储的Key
        String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNum;

        // 3.从Redis读取Key对应的value
        ResultEntity<String> resultEntity = redisRemoteService.getRedisStringValueByKey(key);

        // 4.检查查询操作是否有效
        String result = resultEntity.getResult();
        if (ResultEntity.FAILED.equals(result)) {

            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, resultEntity.getMessage());
            return "member-reg";
        }

        String redisCode = resultEntity.getData();
        if (redisCode == null) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_CODE_NOT_EXISTS);
            return "member-reg";
        }

        // 5.能从Redis查询到，则比较两者是否一致
        String formCode = memberVO.getCode();
        if (!formCode.equals(redisCode)) {

            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_CODE_INVALID);
        }
        // 6.一致，将value从Redis中删除
        ResultEntity<String> removeResultEntity = redisRemoteService.removeRedisKeyRemote(key);
    /*if (ResultEntity.FAILED.equals(removeResultEntity.getResult())) {
        return "";
    }*/

        // 7.执行密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        memberVO.setUserpswd(passwordEncoder.encode(memberVO.getUserpswd()));

        // 8. 执行保存
        MemberPO memberPO = new MemberPO();
        BeanUtils.copyProperties(memberVO, memberPO);
        ResultEntity<String> saveMemberResultEntity = mySQLRemoteService.saveMember(memberPO);

        if (ResultEntity.FAILED.equals(saveMemberResultEntity)) {

            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, saveMemberResultEntity.getMessage());

            return "member-reg";
        }

        return "redirect:http://localhost:81/auth/member/to/login/page";
    }

    // 登录
    @RequestMapping("/auth/member/do/login")
    public String doLogin(@RequestParam("loginacct") String logincacct,
                          @RequestParam("userpswd") String userpswd,
                          ModelMap modelMap,
                          HttpSession session) {

        // 1.调用远程接口查询登录账号
        ResultEntity<MemberPO> resultEntity = mySQLRemoteService.getMemberPOByLoginAcctRemote(logincacct);

        // 2.失败则返回登录页面
        if (ResultEntity.FAILED.equals(resultEntity.getResult())) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_EXCEPTION, resultEntity.getMessage());
            return "member-login";
        }

        MemberPO memberPO = resultEntity.getData();

        if (memberPO == null) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_EXCEPTION, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 3.比较密码
        String userpswdDB = memberPO.getUserpswd();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(userpswd, userpswdDB);
        if (!matches) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        // 4.创建MemberLoginVO对象存入Session域
        MemberLoginVO memberLoginVO = new MemberLoginVO(memberPO.getId(), memberPO.getUsername(), memberPO.getEmail());
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, memberLoginVO);

        return "redirect:http://localhost:81/auth/member/to/center/page";
    }

    // 退出
    @RequestMapping("/auth/member/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:http://localhost:81/";
    }
}
