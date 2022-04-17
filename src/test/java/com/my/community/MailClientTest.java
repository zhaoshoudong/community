package com.my.community;

import com.my.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MailClientTest {
    @Autowired
    MailClient mailClient;
    @Autowired
    TemplateEngine templateEngine;
    //发送邮件
    @Test
    public void testMail(){
        mailClient.sendMail("649035198@qq.com","妹妹","猜猜我是谁？");
    }
    //利用 TemplateEngine 生成Html;发送HTML邮件
    @Test
    public void testHtml(){
        Context context = new Context();
        context.setVariable("username","Sunday");
        String process = templateEngine.process("/mail/activation", context);
        mailClient.sendMail("2509434424@qq.com","HTML",context.toString());
        System.out.println(process);
    }
}
