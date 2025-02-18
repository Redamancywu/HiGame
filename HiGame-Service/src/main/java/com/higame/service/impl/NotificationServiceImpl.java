package com.higame.service.impl;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotificationServiceImpl {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private IAcsClient acsClient;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${aliyun.sms.sign-name}")
    private String signName;

    @Value("${aliyun.sms.template-code}")
    private String templateCode;

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mailFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }

    public void sendVerificationEmail(String to, String code) throws MessagingException {
        String subject = "HiGame验证码";
        String content = String.format(
                "<div style='text-align: center; font-family: Arial, sans-serif;'>"
                + "<h2>HiGame验证码</h2>"
                + "<p>您的验证码是：</p>"
                + "<h1 style='color: #4CAF50;'>%s</h1>"
                + "<p>验证码有效期为5分钟，请勿泄露给他人。</p>"
                + "</div>",
                code);
        sendEmail(to, subject, content);
    }

    public void sendSms(String phoneNumber, String code) throws ClientException {
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumber);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam(String.format("{\"code\":\"%s\"}", code));

        SendSmsResponse response = acsClient.getAcsResponse(request);
        if (!"OK".equals(response.getCode())) {
            throw new RuntimeException("短信发送失败：" + response.getMessage());
        }
    }
}