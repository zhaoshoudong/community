package com.my.community;

import com.my.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive() {
        String text = "这里可以☆赌☆博,可以☆吸☆毒☆,可以☆嫖☆娼☆,可以☆开☆票☆,哈哈哈,☆嫖☆娼☆a";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);

        String text2 = "☆f☆a☆b☆ca";
        String filter2 = sensitiveFilter.filter(text2);
        System.out.println(filter2);

    }
}
