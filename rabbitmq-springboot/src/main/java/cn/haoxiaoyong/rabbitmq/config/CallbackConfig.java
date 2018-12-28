package cn.haoxiaoyong.rabbitmq.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by haoxy on 2018/12/28.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 消息发布确定
 * 通过实现ConfirmCallback接口
 */
@Component
public class CallbackConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {


    private static Logger logger = LoggerFactory.getLogger(CallbackConfig.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        //在这里可以将这些日志保存到数据库
        /**
         * ack: 如果消息发送成功返回true,否则返回 false
         */
        if (ack) {
            logger.info("send message successful !");
        } else {
            logger.info("send message failed");
            System.out.println("消息的唯一标识: " + correlationData.getId());
            System.out.println("失败原因: " + cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        //当消息发送失败的时候会执行以下
        logger.info(message.getMessageProperties().getConsumerTag() + "send message failed");
        System.out.println("消息主体 message" + message);
        System.out.println("消息主体 replyCode" + replyCode);
        System.out.println("描述" + replyText);
        System.out.println("消息使用的交换机" + exchange);
        System.out.println("消息使用的路由键" + routingKey);
    }
}
