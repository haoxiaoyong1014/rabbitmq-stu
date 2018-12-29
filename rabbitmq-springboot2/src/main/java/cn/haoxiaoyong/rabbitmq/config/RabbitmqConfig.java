package cn.haoxiaoyong.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haoxy on 2018/12/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Configuration
public class RabbitmqConfig {

    @Value("${rabbit.message_queue}")
    private String message_queue;
    @Value("${rabbit.topic_exchange}")
    private String topic_exchange;
    @Value("${rabbit.routing_key}")//routing_message
    private String routing_message;

    //声明队列
    @Bean
    public Queue emailQueue() {
        return new Queue(message_queue);
    }

    //声明交换机
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(topic_exchange);
    }

    //进行交换机和队列之间的绑定
    @Bean
    public Binding bindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(exchange()).with(routing_message);
    }
}
