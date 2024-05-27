package com.mysite.sbb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;

@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionService questionService;

    @Test
    void testJpa() {
        for (int i = 1; i <= 10; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
			SiteUser user1 = null;
			questionService.create(subject, content, user1, 0);
        }
        
    }
    
    @Test
    void testJpa1() {
        for (int i = 1; i <= 10; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
			SiteUser user1 = null;
			questionService.create(subject, content, user1, 1);
        }
        
    }
    
    @Test
    void testJpa2() {
        for (int i = 1; i <= 10; i++) {
            String subject = String.format("테스트 데이터입니다:[%03d]", i);
            String content = "내용무";
			SiteUser user1 = null;
			questionService.create(subject, content, user1, 2);
        }
        
    }
}
