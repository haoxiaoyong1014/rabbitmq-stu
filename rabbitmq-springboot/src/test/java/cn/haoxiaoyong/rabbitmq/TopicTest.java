package cn.haoxiaoyong.rabbitmq;

import cn.haoxiaoyong.rabbitmq.producer.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by haoxy on 2018/12/27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TopicTest {

    @Autowired
    private TopicProducer topicProducer;

    @Test
    public void test_all(){
        topicProducer.testProducer_all();
    }


    @Test
    public void test_email(){
        topicProducer.testProducer_email();

    }
    @Test
    public void test_sms(){
        topicProducer.testProducer_sms();
    }



}
