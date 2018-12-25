package cn.haoxiaoyong.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by haoxy on 2018/12/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * rabbitmq的入门程序-生产者
 * <p>
 * 从新认识一下 RabbitMq
 */
public class Producer01 {

    private static final String QUEUE = "helloworld";

    public static void main(String[] args) {
        //通过连接工厂创建一个新的链接和 mq 建立一个链接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("www.haoxiaoyong.cn");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置一个虚拟机,一个 mq可以设置多个虚拟机,每个虚拟机相当于一个独立的 mq,可以模拟多个 mq
        connectionFactory.setVirtualHost("/");
        Connection connection = null;
        Channel channel = null;
        try {
            //建立新链接
            connection = connectionFactory.newConnection();
            //创建通道,生产者和 mq 的所以通信都是在通道中进行
            channel = connection.createChannel();
            //声明一个队列,
            //参数: String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            /**
             * 参数明细
             * queue: 队列名称
             * durable: 是否持久化,如果开启持久化,mq重启后队列还在
             * exclusive: 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列则自动删除,如果将此参数设置true可用于临时队列的创建
             * autoDelete: 自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）
             * arguments: 参数，可以设置一个队列的扩展参数，比如：可设置存活时间
             */
            channel.queueDeclare(QUEUE, true, false, false, null);
            //发送消息
            //参数: String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * 参数明细
             * exchange: 交换机,如果不指定将使用 mq 默认的交换机(设置为: "")
             * routingKey: 路由键,交换机根据路由键来将消息转发到指定的队列,如果使用默认交换机，routingKey设置为队列的名称
             * props: 消息的属性
             * body: 消息内容
             */
            //消息内容
            for (int i = 0; i < 7; i++) {
                String message = "hello world haoxiaoyong";

                channel.basicPublish("", QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭连接
            //关闭通道
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
