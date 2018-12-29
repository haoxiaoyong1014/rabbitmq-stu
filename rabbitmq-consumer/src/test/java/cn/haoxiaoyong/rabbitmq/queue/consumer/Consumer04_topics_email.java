package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by haoxy on 2018/12/26.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Consumer04_topics_email {

    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    //交换机
    private static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    //路由 key
    private static final String ROUTINGKEY_EMAIL="inform.#.email.#";

    public static void main(String[] args) throws IOException, TimeoutException {

        //通过连接工厂创建新的连接和 mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("www.haoxiaoyong.cn");
        //设置虚拟机,一个 mq服务可以设置多个虚拟机,每个虚拟机相当于一个独立的 mq
        connectionFactory.setVirtualHost("/");
        //建立新连接
        Connection connection = connectionFactory.newConnection();
        //创建会话通道,生产者和mq服务所有通信都在channel通道中完成
        Channel channel = connection.createChannel();
        channel.basicQos(1);
        //声明队列
        channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);
        //进行交换机和队列的绑定
        channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_TOPICS_INFORM,ROUTINGKEY_EMAIL);
        //实现消费方法
        DefaultConsumer defaultConsumer=new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //消息id，mq在channel中用来标识消息的id，可用于确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message= new String(body,"utf-8");
                System.out.println("receive message:"+message);

            }
        };
        //监听队列
        channel.basicConsume(QUEUE_INFORM_EMAIL,true,defaultConsumer);
    }
}
