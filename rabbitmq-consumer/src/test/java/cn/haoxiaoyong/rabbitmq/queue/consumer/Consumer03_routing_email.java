package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by haoxy on 2018/12/26.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 路由键
 */
public class Consumer03_routing_email {

    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    //交换机
    private static final String EXCHANGE_ROUTING_INFORM = "exchange_routing_inform";
    //路由键
    private static final String ROUTINGKEY_EMAIL = "inform_email";

    public static void main(String[] args) throws IOException, TimeoutException {
        //通过连接工厂创建新的连接和 mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("haoxy");
        connectionFactory.setPassword("haoxy");
        connectionFactory.setHost("47.100.102.136");
        //设置虚拟机,一个 mq服务可以设置多个虚拟机,每个虚拟机相当于一个独立的 mq
        connectionFactory.setVirtualHost("/");
        //建立新连接
        Connection connection = connectionFactory.newConnection();
        //创建会话通道,生产者和mq服务所有通信都在channel通道中完成
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
        //声明交换机
        /**
         * 此案例交换机类型为: direct
         */
        channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
        //进行交换机和队列绑定
        //参数：String queue, String exchange, String routingKey
        /**
         * 参数明细：
         * 1、queue 队列名称
         * 2、exchange 交换机名称
         * 3、routingKey 路由key，作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中调协为空字符串
         */
        channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
        //实现消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //路由键
                String exchange1 = envelope.getExchange();
                //消息 id,手动 ack的时候要用到
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message= new String(body,"utf-8");
                System.out.println("receive message:"+message);
            }
        };
        //监听队列
        //参数：String queue, boolean autoAck, Consumer callback
        /**
         * 参数明细：
         * 1、queue 队列名称
         * 2、autoAck 自动回复，当消费者接收到消息后要告诉mq消息已接收，如果将此参数设置为tru表示会自动回复mq，如果设置为false要通过编程实现回复
         * 3、callback，消费方法，当消费者接收到消息要执行的方法
         */
        channel.basicConsume(QUEUE_INFORM_EMAIL,true,defaultConsumer);
    }

}
