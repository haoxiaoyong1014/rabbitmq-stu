package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by haoxy on 2018/12/27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public class TopicSmsConsumer {

    @RabbitHandler
    @RabbitListener(queues = {"queue_inform_sms"})////inform.#.sms.#
    public void receiverSms(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {

        try {
            System.out.println("sms接收到消息" + msg);
            channel.basicAck(tag, false);
            System.out.println("sms_处理消息完毕....");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
