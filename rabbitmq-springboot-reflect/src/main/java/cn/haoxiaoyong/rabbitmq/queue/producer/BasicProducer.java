package cn.haoxiaoyong.rabbitmq.queue.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by haoxy on 2018/12/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public abstract class BasicProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //交换机
    @Value("${rabbit.routing_key}")//routing_message
    private String routing_message;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * @param serviceName:              类名
     * @param serviceMethodName:类名中的方法名
     * @param correlationId: 应用程序使用-关联标识符
     * @param msg:消息内容
     */
    public void sendMessage(String serviceName, String serviceMethodName, String correlationId, Object msg) {
        this.rabbitTemplate.setCorrelationKey(correlationId);
        //参数: String routingKey, Object message, MessagePostProcessor messagePostProcessor
        this.rabbitTemplate.convertAndSend(routing_message, msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                long tag = new Random().nextLong();
                message.getMessageProperties().setDeliveryTag(tag);
                message.getMessageProperties().setTimestamp(new Date());
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setHeader("serviceName",serviceName);
                message.getMessageProperties().setHeader("serviceMethodName",serviceMethodName);
                System.out.println("Random 随机数:" + tag);
                return message;
            }
        }, new CorrelationData(correlationId));

    }


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {

        logger.info("BasicProducer中sengMessage方法执行...");
        //在这里可以将这些日志保存到数据库
        /**
         * correlationData:消息唯一标识
         * ack: 如果消息发送成功返回true,否则返回 false
         * cause:失败原因
         */
        if (ack) {
            logger.info("Message successfully arrived exchange !");
        } else {
            logger.info("send message failed");
            System.out.println("消息的唯一标识: " + correlationData.getId());
            System.out.println("失败原因: " + cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        //当消息发送失败的时候会执行以下
        logger.info(message.getMessageProperties().getDeliveryTag() + "send message failed");
        System.out.println("消息主体 message" + message);
        System.out.println("消息主体 replyCode" + replyCode);
        System.out.println("描述" + replyText);
        System.out.println("消息使用的交换机" + exchange);
        System.out.println("消息使用的路由键" + routingKey);
    }
}
