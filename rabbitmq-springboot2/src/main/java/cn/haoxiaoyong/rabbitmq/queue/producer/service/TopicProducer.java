package cn.haoxiaoyong.rabbitmq.queue.producer.service;

import cn.haoxiaoyong.rabbitmq.queue.producer.BasicProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

/**
 * Created by haoxy on 2018/12/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Service
public class TopicProducer extends BasicProducer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void send(){
        logger.info("TopicProducer中send方法执行...");

    }

}
