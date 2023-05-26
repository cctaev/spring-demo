# 使用SpringBoot发送邮件

1. 添加Maven依赖
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```


# UT
使用163邮箱的两个账户相互发送邮件。
1. 发送一封普通文本的邮件。
2. 发送一封带附件的普通邮件。
3. 发送一封简单的HTML邮件。
4. 发送一封带多个附件、能展示图片、超链接、表格的HTML邮件。
5. 向3个账户群发一封邮件，并处理发送失败的账户。


# 翻译自SpringFramework文档
https://docs.spring.io/spring-framework/docs/5.3.27/reference/html/integration.html#mail

这一章节描述如何使用SpringFramework发送邮件。

> 类库依赖
> 如果你要使用SpringFramework的Email支持能力，需要在你的类路径中添加如下的jar包。
> The JavaMail / Jakarta Mail 1.6 library
> 这个类库可以在网络上免费获得——例如，在Maven中心的`com.sun.mail:jakarta.mail`
> 请确保使用最新的1.6版本（它使用javax.mail包名称空间）而不是JakartaMail2.0（它使用jakarta.mail包名称空间）
> 请查阅 Jakarta Mail API 代码仓的1.x分支。

SpringFramework提供了有用的工具类库用来发送邮件，它帮助你屏蔽了邮件系统的底层细节并代表客户端负责低级资源的处理。

`org.springframework.mail`包是SpringFramework支持邮件发送的根路径包。 负责发送邮件的核心接口是`MailSender`。`SimpleMailMessage`类中囊括了一封简单邮件的各种属性，例如from和to等属性。 这个包还包含了一套检查型异常体系，以提供邮件系统异常的高级抽象，根异常是`MailException`。 查看doc文档获取异常体系的更多信息https://docs.spring.io/spring-framework/docs/5.3.27/javadoc-api/org/springframework/mail/MailException.html

`org.springframework.mail.javamail.JavaMailSender`接口添加了一些JavaMail的特性，例如MIME Message的支持。`JavaMailSender`同样提供了一个回调接口叫做`org.springframework.mail.javamail.MimeMessagePreparator`用来准备MimeMessage。

## 用法
假如我们有一个叫做`OrderManager`的接口。
```java
public interface OrderManager {
    void placeOrder(Order order);
}
```
再进一步假设，我们有一个需求，要根据用户下的订单，生成一个相关的订单号并发送邮件给用户。

### MailSender和SimpleMailMessage的基础用法
```java
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SimpleOrderManager implements OrderManager {

    private MailSender mailSender;
    private SimpleMailMessage templateMessage;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setTemplateMessage(SimpleMailMessage templateMessage) {
        this.templateMessage = templateMessage;
    }

    public void placeOrder(Order order) {

        // Do the business calculations...

        // Call the collaborators to persist the order...

        // Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(order.getCustomer().getEmailAddress());
        msg.setText(
            "Dear " + order.getCustomer().getFirstName()
                + order.getCustomer().getLastName()
                + ", thank you for placing order. Your order number is "
                + order.getOrderNumber());
        try {
            this.mailSender.send(msg);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

}
```
下面的例子是为前面的代码准备的bean定义
```xml
<beans>
    <bean id="mailSender" class="org.springframework.mail.javamail.JavaMailSenderImpl">
        <property name="host" value="mail.mycompany.example"/>
    </bean>
    
    <!-- this is a template message that we can pre-load with default state -->
    <bean id="templateMessage" class="org.springframework.mail.SimpleMailMessage">
        <property name="from" value="customerservice@mycompany.example"/>
        <property name="subject" value="Your order"/>
    </bean>
    
    <bean id="orderManager" class="com.mycompany.businessapp.support.SimpleOrderManager">
        <property name="mailSender" ref="mailSender"/>
        <property name="templateMessage" ref="templateMessage"/>
    </bean>
</beans>
```
### 使用 Using JavaMailSender 和 MimeMessagePreparator
这节描述了使用MimeMessagePreparator回调接口编写的另一种OrderManager的实现。
在下面的例子中，`mailSender`属性是`JavaMailSender`类型，因此我们可以使用JavaMail的`MimeMessage`类。

```java
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class SimpleOrderManager implements OrderManager {

    private JavaMailSender mailSender;

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void placeOrder(final Order order) {
        // Do the business calculations...
        // Call the collaborators to persist the order...

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO,
                        new InternetAddress(order.getCustomer().getEmailAddress()));
                mimeMessage.setFrom(new InternetAddress("mail@mycompany.example"));
                mimeMessage.setText("Dear " + order.getCustomer().getFirstName() + " " +
                        order.getCustomer().getLastName() + ", thanks for your order. " +
                        "Your order number is " + order.getOrderNumber() + ".");
            }
        };

        try {
            this.mailSender.send(preparator);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

}
```

> 这个邮件代码是一个横切关注点并且可以很好的重构为一个自定义的[Spring AOP](https://docs.spring.io/spring-framework/docs/5.3.27/reference/html/core.html#aop)切面，这个切面可以适合的切入到`OrderManager`对象中。

SpringFramework支持JavaMail的所有实现，可以参考相关的文档获得详细信息。

## 使用JavaMail的`MimeMessageHelper`
`org.springframework.mail.javamail.MimeMessageHelper`可以很好的帮助处理JavaMail，它帮助你很好的屏蔽了啰嗦的JavaMail API。使用MimeMessageHelper可以很容易得创建MimeMessage，就像下面的代码所示
```java
// of course you would use DI in any real-world cases
JavaMailSenderImpl sender = new JavaMailSenderImpl();
sender.setHost("mail.host.com");

MimeMessage message = sender.createMimeMessage();
MimeMessageHelper helper = new MimeMessageHelper(message);
helper.setTo("test@host.com");
helper.setText("Thank you for ordering!");

sender.send(message);
```

### 发送带附件和内联资源的邮件
多部分的Email消息可以同时包含附件和内联资源。例如你想在你的邮件中以内联资源的方式使用图片或样式表，而不是以附件的方式展示它们。

**附件**
下面的代码向你展示了如何使用MimeMessageHelper发送一封带JPEG图片附件的邮件消息。
```java
JavaMailSenderImpl sender = new JavaMailSenderImpl();
sender.setHost("mail.host.com");

MimeMessage message = sender.createMimeMessage();

// use the true flag to indicate you need a multipart message
MimeMessageHelper helper = new MimeMessageHelper(message, true);
helper.setTo("test@host.com");

helper.setText("Check out this image!");

// let's attach the infamous windows Sample file (this time copied to c:/)
FileSystemResource file = new FileSystemResource(new File("c:/Sample.jpg"));
helper.addAttachment("CoolImage.jpg", file);

sender.send(message);
```

**内联资源**
下面的代码向你展示了如何使用MimeMessage发送一封带内联图片资源的邮件消息。
```java
JavaMailSenderImpl sender = new JavaMailSenderImpl();
sender.setHost("mail.host.com");

MimeMessage message = sender.createMimeMessage();

// use the true flag to indicate you need a multipart message
MimeMessageHelper helper = new MimeMessageHelper(message, true);
helper.setTo("test@host.com");

// use the true flag to indicate the text included is HTML
helper.setText("<html><body><img src='cid:identifier1234'></body></html>", true);

// let's include the infamous windows Sample file (this time copied to c:/)
FileSystemResource res = new FileSystemResource(new File("c:/Sample.jpg"));
helper.addInline("identifier1234", res);

sender.send(message);
```
> 内联资源通过使用指定的Content-Id来添加到MimeMessage中（就像上面例子中的identifier1234)。
> 向MimeMessage中添加文本和内联资源的顺序非常重要。确保先添加文本，再添加资源。如何添加错了，就会出错。

### 通过模板类库来创建邮件内容
上面的代码明确的展示了创建邮件内容，通过使用像`message.setText()`的方法。这对于简单的例子来说很好，例如上面的例子就没问题，但它只是用来展示如何使用这些基础的API。

一般在企业应用中，开发者一般不会使用上面的那种方式创建邮件内容并发送。原因如下：
- 创建基于HTML的邮件非常乏味且容易出错。
- 展示逻辑和业务逻辑没有清晰的分离。
- 改变邮件展示结构的时候，需要修改Java代码，重新编译、部署等等。

通常，解决上面这些问题的方法是使用模板类库（例如FreeMarker）来定义邮件内容的展示结构。这让你的代码专注于生成数据并渲染到邮件消息中发送出去。这是一个最佳实现，当你的邮件内容非常复杂时。由于SpringFramework支持FreeMarker，所以这很容易做到。


