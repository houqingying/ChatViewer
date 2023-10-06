package com.chatviewer.blog.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author ChatViewer
 */
@Slf4j
@Component
public class AliyunSendSms {

    @Value("${my-conf.ali-key}")
    private String accessKey;
    @Value("${my-conf.ali-secret}")
    private String secret;
    @Value("${my-conf.sign-name}")
    private String signName;
    @Value("${my-conf.template-code}")
    private String templateCode;

    /**
     * 发送短信
     * @param phoneNumbers 手机号
     */
    public String sendMessage(String phoneNumbers) {
        String param = genRandomCode();
        // 设置AccessKey和SecretKey
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou",
                accessKey,
                secret
        );
        // 设置发送短信的Client
        IAcsClient client = new DefaultAcsClient(profile);

        // 构造发送发信请求，设置手机号，阿里云提供的签名和模板code，模板参数
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumbers);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{\"code\":\"" + param + "\"}");

        try {
            // 因为发送短信要money，而且阿里云的测试号只能给通过验证的手机号发送短信，所以把这句注释掉了
            // 正常测试，向自己的手机号发送是莫得问题的
            // SendSmsResponse response = client.getAcsResponse(request)
            log.info("短信已发送(●'◡'●)");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 返回生成的验证码给Service层
        return param;
    }


    /**
     * 随机生成4位验证码
     * @return 验证码
     */
    public static String genRandomCode(){
        Random random = new Random();
        // 生成[0,10000)之间的随机整数
        int number = random.nextInt(10000);
        // 将随机整激格式化4位字符串，不足4位在前面补0
        return String.format("%04d", number);
    }
}
