package cn.haoxiaoyong.rabbitmq.consumer.producer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haoxy on 2018/12/26.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Configuration
public class RabbitMqConfig {

    @Value("${myRabbitmq.QUEUE_INFORM_EMAIL}")
    private String QUEUE_INFORM_EMAIL;

    @Value("${myRabbitmq.QUEUE_INFORM_SMS}")
    private String QUEUE_INFORM_SMS;

    @Value("${myRabbitmq.EXCHANGE_TOPICS_INFORM}")
    private String EXCHANGE_TOPICS_INFORM;

    @Value("${myRabbitmq.ROUTINGKEY_EMAIL}")
    private String ROUTINGKEY_EMAIL;

    @Value("${myRabbit.ROUTINGKEY_SMS}")
    private String ROUTINGKEY_SMS;
}
