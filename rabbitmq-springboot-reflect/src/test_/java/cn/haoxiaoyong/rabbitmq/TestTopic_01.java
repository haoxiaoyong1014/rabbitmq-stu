package cn.haoxiaoyong.rabbitmq;

import cn.haoxiaoyong.rabbitmq.queue.producer.service.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by haoxiaoyong on 2018/12/30 下午 9:18
 * e-mail: hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * Blog: 47.100.102.136
 */
@SpringBootTest(classes = RabbitmqApplication.class)
@RunWith(SpringRunner.class)
public class TestTopic_01 {

    @Autowired
    private TopicProducer topicProducer;

    @Test
    public void testTopic_01(){
        topicProducer.send();
    }

}
