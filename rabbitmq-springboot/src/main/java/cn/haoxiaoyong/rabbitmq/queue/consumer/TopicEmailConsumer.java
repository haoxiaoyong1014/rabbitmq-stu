package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.CorrelationData;
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
public class TopicEmailConsumer {

    @RabbitHandler
    @RabbitListener(queues = {"queue_inform_email"})//inform.#.email.#
    public void receiverEmail(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        channel.basicQos(1);
        //接收到消息
        System.out.println("email接收到消息" + msg + "开始处理消息....");
        //处理业务逻辑.....
       /* if (msg.contains("message")) {
            throw new RuntimeException();
        }*/
        try {
            //消息确定
            channel.basicAck(tag, false);
            System.out.println("email_消息ID:" + tag + "处理消费完毕...");
        } catch (IOException e) {
            /**
             * deliveryTag: 该消息的index
             * multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
             * requeue：被拒绝的是否重新入队列，true 放在队首,false 消息进入绑定的DLX。一定注意：若此消息一直Nack重入队会导致的死循环
             */
            //channel.basicNack(tag, false, true);
            e.printStackTrace();
        }
    }
}
