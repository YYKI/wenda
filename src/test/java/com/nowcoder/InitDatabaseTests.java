package com.nowcoder;

import com.nowcoder.dao.QuestionDao;
import com.nowcoder.dao.UserDao;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
//@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDao userDao;
    @Autowired
    QuestionDao questionDao;
    @Test
    public void addUserTest(){
        Random random = new Random();
        for(int i=0; i<11; i++){
            User user = new User();
            user.setName(String.format("USER%d", i));
            user.setPassword("123456");
            user.setSalt("123");
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            userDao.addUser(user);
        }
    }

    @Test
    public void addQuestion(){
        for(int i=1; i<12; i++){
            Question question = new Question();
            question.setContent(String.format("this is content %d", i));
            question.setTitle("title "+i);
            question.setUserId(i);
            Date date = new Date();
            date.setTime(date.getTime()+1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setCommentCount(i);
            questionDao.addQuestion(question);
        }
    }

    @Test
    public void deleteTest(){
//        userDao.deleteById(11);
        User user = userDao.selectById(4);
        System.out.println(user.getName());
        user.setPassword("654321");
        userDao.updateUser(user);


    }
}
