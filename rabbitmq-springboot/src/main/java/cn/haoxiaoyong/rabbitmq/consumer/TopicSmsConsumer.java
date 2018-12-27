package cn.haoxiaoyong.rabbitmq.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created by haoxy on 2018/12/27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public class TopicSmsConsumer {

    @RabbitHandler
    @RabbitListener(queues = {"queue_inform_sms"})////inform.#.sms.#
    public void receiverSms(String msg){

        System.out.println("sms接收到消息"+msg);
    }
}
