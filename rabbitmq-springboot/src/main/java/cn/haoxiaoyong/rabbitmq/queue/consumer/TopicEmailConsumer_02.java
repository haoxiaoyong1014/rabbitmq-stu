package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by haoxy on 2018/12/27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * */

@Component
public class TopicEmailConsumer_02 {

    @RabbitHandler
    @RabbitListener(queues = {"queue_inform_email"})//inform.#.email.#
    public void receiverEmail(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        //接收到消息
        System.out.println("email_02接收到消息" + msg+"开始处理消息....");
        //处理业务逻辑.....
        try {
            //消息确定
            channel.basicAck(tag, false);
            System.out.println("email_02_消息ID: " + tag + " 处理消费完毕...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
