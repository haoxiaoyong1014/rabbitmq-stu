package cn.haoxiaoyong.rabbitmq.aware;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by haoxy on 2018/12/29.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Component
public class RabbitMqListenerAware implements ChannelAwareMessageListener, ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ApplicationContext applicationContext;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        try {
            logger.info("onMessage方法执行......" + message.getMessageProperties());
            Object bean = applicationContext.getBean(message.getMessageProperties().getHeaders().get("serviceName").toString());
            String methodName = message.getMessageProperties().getHeaders().get("serviceMethodName").toString();
            Method method = bean.getClass().getMethod(methodName, String.class,Message.class,Channel.class);
            String msg = new String(message.getBody(), "utf-8");
            method.invoke(bean, msg,message,channel);
        } catch (IllegalAccessException | NoSuchMethodException | UnsupportedEncodingException | NullPointerException |InvocationTargetException e) {
            logger.info("-----------发生error......." + e.getMessage());
            //参数明细: long deliveryTag: 消息唯一标识的id, boolean multiple: 是否批量处理, boolean requeue: 是否重新入队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

