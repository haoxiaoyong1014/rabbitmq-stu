package cn.haoxiaoyong.rabbitmq.config;

import cn.haoxiaoyong.rabbitmq.aware.RabbitMqListenerAware;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageHandler;

import java.util.HashMap;
import java.util.Map;

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



    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(topic_exchange);
        rabbitTemplate.setRoutingKey(routing_message);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,Queue... queues){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queues);
        MessageListenerAdapter adapter = new MessageListenerAdapter(new RabbitMqListenerAware());
        //设置处理器的消费消息的默认方法,如果没有设置，那么默认的处理器中的默认方式是handleMessage方法
        adapter.setDefaultListenerMethod("onMessage");
        container.setMessageListener(adapter);
        return container;
    }

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
