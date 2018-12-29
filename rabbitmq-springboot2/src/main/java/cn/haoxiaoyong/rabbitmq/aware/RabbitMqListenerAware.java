package cn.haoxiaoyong.rabbitmq.aware;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by haoxy on 2018/12/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public class RabbitMqListenerAware implements ChannelAwareMessageListener,ApplicationContextAware {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println("onMessage执行.........");


    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
