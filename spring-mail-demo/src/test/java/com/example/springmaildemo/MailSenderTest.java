package com.example.springmaildemo;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.ResourceUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    public void test_send_plain_text_with_attachment_mail() throws MessagingException, FileNotFoundException {
        // 1. 创建发送器实例
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 2. 设置邮箱属性
        mailSender.setHost(mailProperties.getProperty("smtp.server"));
        mailSender.setUsername(mailProperties.getProperty("username"));
        mailSender.setPassword(mailProperties.getProperty("password"));
        // 3. 创建mime消息
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        // 4. 使用Helper增强mime，第二个属性传入true，表示该邮件内容是multipart
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
        // 5. 设置发件人、收款人
        messageHelper.setFrom("15071345174@163.com");
        messageHelper.setTo("cctaev@163.com");
        // 6. 设置主题
        messageHelper.setSubject("这是一封带附件的普通邮件");
        // 7. 添加文本
        messageHelper.setText("Hello!， 这是一封普通邮件。");
        // 8. 添加附件
        File attachment = ResourceUtils.getFile("classpath:images/landscape.jpg");
        // ** 注意文件名要带后缀，因为用户收到的邮件中，附件文件名就是这里定义的文件名。
        messageHelper.addAttachment("LandScape.jpg", attachment);
        // 9. 发送
        mailSender.send(mimeMessage);
    }

    /**
     * 发送一封简单的HTML邮件
     */
    @Test
    public void test_send_html_text_mail() throws MessagingException {
        // 1. 创建邮件发送器实例
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 2. 设置邮箱信息
        mailSender.setHost(mailProperties.getProperty("smtp.server"));
        mailSender.setUsername(mailProperties.getProperty("username"));
        mailSender.setPassword(mailProperties.getProperty("password"));

        // 3. 创建mimeMessage
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        // 4. 使用Helper增强mimeMessage
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

        // 5. 设置发送人、接收人、主题、邮件内容
        helper.setFrom("15071345174@163.com");
        helper.setTo("cctaev@163.com");
        helper.setSubject("这是一封简单的HTML邮件");

        String htmlText = "<html><body><h1>这是一号标题！</h1></body></html>";
        // ** 注意需要将第二个参数设置为true，表示这是一封html邮件，其实是将Content-Type设置为text/html
        helper.setText(htmlText, true);

        // 6. 发送
        mailSender.send(mimeMessage);
    }

    /**
     * 发送一封带多个附件、能展示图片、超链接、表格的HTML邮件
     */
    @Test
    public void test_send_html_text_with_multi_attachments_mail() throws MessagingException, IOException {
        // 1. 创建邮件发送器实例，并设置邮箱登录信息
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getProperty("smtp.server"));
        mailSender.setUsername(mailProperties.getProperty("username"));
        mailSender.setPassword(mailProperties.getProperty("password"));
        // 2. 创建mime消息，并用Helper增强
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

        // 3. 使用helper设置发送人、接收人、主题、正文、附件
        helper.setFrom("15071345174@163.com");
        helper.setTo("cctaev@163.com");
        helper.setSubject("这是一封带多个附件、能展示图片、超链接、表格的HTML邮件");

        File htmlMailFile = ResourceUtils.getFile("classpath:template/mail-one.html");
        String htmlMailContent = FileUtils.readFileToString(htmlMailFile, StandardCharsets.UTF_8);
        helper.setText(htmlMailContent, true);

        File file1 = ResourceUtils.getFile("classpath:images/landscape.jpg");
        File file2 = ResourceUtils.getFile("classpath:images/LandScape.zip");
        // ** 主题：添加内联资源是addInline，这样html里面才能通过cid引用到内敛资源
        helper.addInline("landscape.jpg", file1);
        // 添加附件使用addAttachment
        helper.addAttachment("landscape.jpg", file1);
        helper.addAttachment("LandScape.zip", file2);


        // 4. 发送
        mailSender.send(mimeMessage);
    }

    /**
     * 向2个账户群发一封邮件
     */
    @Test
    public void test_send_to_multi_user() throws MessagingException {
        // 1. 创建邮件发送器实例，设置邮箱登录信息
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getProperty("smtp.server"));
        mailSender.setUsername(mailProperties.getProperty("username"));
        mailSender.setPassword(mailProperties.getProperty("password"));
        // 2. 创建mime消息，并用Helper增强
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

        // 3. 使用helper设置发送人、接收人、主题、正文
        helper.setFrom("15071345174@163.com");
        helper.setTo(new String[]{"cctaev@163.com", "cctaev@aliyun.com"});
        helper.setSubject("这是一封群发的邮件");
        helper.setText("这封邮件发送给了2个人");
        // 4. 发送
        mailSender.send(mimeMessage);
    }
}
