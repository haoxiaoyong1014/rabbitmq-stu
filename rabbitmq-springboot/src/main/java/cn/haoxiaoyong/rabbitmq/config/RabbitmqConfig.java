package cn.haoxiaoyong.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haoxy on 2018/12/27.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Configuration
public class RabbitmqConfig {

    //邮件队列名称
    @Value("${myRabbit.queue_inform_email}")
    private String queue_inform_email;

    //短信队列名称
    @Value("${myRabbit.queue_inform_sms}")
    private String queue_inform_sms;

    //交换机
    @Value("${myRabbit.exchange_topics_inform}")
    private String exchange_topics_inform;


    //声明邮件队列
    @Bean
    public Queue queueEmail() {
        return new Queue(queue_inform_email);
    }

    //声明短信队列
    @Bean
    public Queue queueSms() {
        return new Queue(queue_inform_sms);
    }

    //声明交换机
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange_topics_inform);
    }

    //进行邮件队列和交换机的绑定
    @Bean
    public Binding bindingEmail(TopicExchange topic,Queue queueEmail) {
        return BindingBuilder.bind(queueEmail).to(topic).with("inform.#.email.#");//inform_email
    }

    //进行短信队列和交换机的绑定
    @Bean
    public Binding bindingSms(TopicExchange topic,Queue queueSms) {
        return BindingBuilder.bind(queueSms).to(topic).with("inform.#.sms.#");//inform_sms
    }

}
