package cn.haoxiaoyong.rabbitmq.queue.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
    public void testProducer_all() {

        String message = "this is message";

        //参数: String exchange, String routingKey, Object object
        /**
         * exchange:交换机
         * routingKey: 路由键
         * object: 消息内容
         * CorrelationDate: 消息的唯一标识
         *
         * convertAndSend();将参数对象转换为org.springframework.amqp.core.Message后发送
         * convertSendAndReceive(): 转换并发送消息,且等待消息者返回响应消息。
         */

        //Object response = this.rabbitTemplate.convertSendAndReceive(exchange_topics_inform, "inform.email.sms", message, new CorrelationData(UUID.randomUUID().toString()));
        //System.out.println("打印消费者返回的内容: "+response);
        this.rabbitTemplate.convertAndSend(exchange_topics_inform, "inform.email.sms", message,new CorrelationData(UUID.randomUUID().toString()));

    }

    //邮箱
    public void testProducer_email() {

        String message = "this is message";

        //参数: String exchange, String routingKey, Object object
        /**
         * exchange:交换机
         * routingKey: 路由键
         * object: 消息内容
         */
        this.rabbitTemplate.convertAndSend(exchange_topics_inform, "inform.abc.email.abc", message);
    }

    //短信
    public void testProducer_sms() {

        String message = "this is message";

        //参数: String exchange, String routingKey, Object object
        /**
         * exchange:交换机
         * routingKey: 路由键
         * object: 消息内容
         */
        this.rabbitTemplate.convertAndSend(exchange_topics_inform, "inform.sms.abc", message);
    }
}
