package cn.haoxiaoyong.rabbitmq.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by haoxy on 2018/12/27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public class TopicProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //交换机
    @Value("${myRabbit.exchange_topics_inform}")
    private String exchange_topics_inform;
    //两个都能接收
    public void testProducer_all(){

        String message="this is message";

        //参数: String exchange, String routingKey, Object object
        /**
         * exchange:交换机
         * routingKey: 路由键
         * object: 消息内容
         */
        this.rabbitTemplate.convertAndSend(exchange_topics_inform,"inform.email.sms",message);
    }
    public void testProducer_email(){

        String message="this is message";

        //参数: String exchange, String routingKey, Object object
        /**
         * exchange:交换机
         * routingKey: 路由键
         * object: 消息内容
         */
        this.rabbitTemplate.convertAndSend(exchange_topics_inform,"inform.abc.email.abc",message);
    }
    public void testProducer_sms(){

        String message="this is message";

        //参数: String exchange, String routingKey, Object object
        /**
         * exchange:交换机
         * routingKey: 路由键
         * object: 消息内容
         */
        this.rabbitTemplate.convertAndSend(exchange_topics_inform,"inform.sms.abc",message);
    }
}
