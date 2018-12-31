package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by haoxy on 2018/12/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public class TopicConsumer {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RabbitHandler
    public void process(String msg, Message message, Channel channel) throws IOException {
        System.out.println("消费者接收到消息");
        logger.info("收到消息...开始处理消息...."+msg);
        logger.info("CorrelationId:"+message.getMessageProperties().getCorrelationId());
        logger.info("MessageId:"+message.getMessageProperties().getMessageId());
        logger.info("DeliveryTag:" +message.getMessageProperties().getDeliveryTag());
        logger.info("消息处理完毕...");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        logger.info("ack完毕......");
    }
}
