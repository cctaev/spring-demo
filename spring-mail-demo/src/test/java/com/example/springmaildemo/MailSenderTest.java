package com.example.springmaildemo;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @author cctaev
 * @since 2023-05-26
 */
public class MailSenderTest {
    @Test
    public void test_send_plain_text_mail() {
        // 1. 创建邮件发送器实例
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 2. 设置地址、账号、密码
        mailSender.setHost("smtp.163.com");
        mailSender.setUsername("15071345174@163.com");
        mailSender.setPassword("ZYBRYAERREPGNCBA");

        // 3. 创建mime消息对象
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 4. 使用Helper包装mime消息
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        try {
            // 5. 设置收件人、发件人
            messageHelper.setFrom("15071345174@163.com");
            messageHelper.setTo("cctaev@163.com");
            // 6. 设置邮件主题
            messageHelper.setSubject("This is SUBJECT!");
            // 7. 调用这个方法会将文本类型设置为text/plain
            messageHelper.setText("Hi, This is a plain text mail! Date: " + new Date());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        mailSender.send(mimeMessage);
        System.out.println("Done");
    }
}
