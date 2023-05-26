package com.example.springmaildemo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

/**
 * @author cctaev
 * @since 2023-05-26
 */
public class MailSenderTest {
    private static final Properties mailProperties = new Properties();

    @BeforeAll
    public static void before() throws IOException {
        URL url = ResourceUtils.getURL("classpath:config/mail.properties");
        mailProperties.load(url.openStream());
    }

    /**
     * 发送一封普通文本的邮件
     */
    @Test
    public void test_send_plain_text_mail() {
        // 1. 创建邮件发送器实例
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 2. 设置地址、账号、密码
        mailSender.setHost(mailProperties.getProperty("smtp.server"));
        mailSender.setUsername(mailProperties.getProperty("username"));
        mailSender.setPassword(mailProperties.getProperty("password"));

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
        // 8. 发送
        mailSender.send(mimeMessage);
        System.out.println("Done");
    }

    /**
     * 发送一封带附件的普通邮件
     */
    @Test
    public void test_send_plain_text_with_attachment_mail() {
        // 1. 创建发送器实例
        // 2. 设置邮箱属性
        // 3. 创建mime消息
        // 4. 使用Helper增强mime
        // 5. 设置发件人、收款人
        // 6. 设置主题
        // 7. 添加文本
        // 8. 添加附件
        // 9. 发送
    }

    /**
     * 发送一封简单的HTML邮件
     */
    @Test
    public void test_send_html_text_mail() {

    }

    /**
     * 发送一封带多个附件、能展示图片、超链接、表格的HTML邮件
     */
    @Test
    public void test_send_html_text_with_multi_attachments_mail() {

    }

    /**
     * 向3个账户群发一封邮件，并处理发送失败的账户。
     */
    @Test
    public void test_send_to_multi_user() {

    }
}
