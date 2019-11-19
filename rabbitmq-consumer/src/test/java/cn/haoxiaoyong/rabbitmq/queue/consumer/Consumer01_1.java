package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by haoxy on 2018/12/24.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * <p>
 * rabbitmq的入门程序-消费者
 */
public class Consumer01_1 {

    //队列
    private static final String QUEUE = "helloworld";

    public static void main(String[] args) {
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("47.100.102.136");
        connectionFactory.setPort(5672);//端口
        connectionFactory.setUsername("haoxy");
        connectionFactory.setPassword("haoxy");
        //设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");
        try {
            //创建一个新连接
            Connection connection = connectionFactory.newConnection();
            //创建会话通道
            final Channel channel = connection.createChannel();
            //声明队列
            //参数: String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            /**
             * queue: 队列名称
             * durable: 是否持久化，如果持久化，mq重启后队列还在
             * exclusive: 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列则自动删除,如果将此参数设置true可用于临时队列的创建
             * autoDelete: 自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）
             * arguments: 参数，可以设置一个队列的扩展参数，比如：可设置存活时间
             */
            channel.queueDeclare(QUEUE, true, false, false, null);
            //参数: int prefetchCount=1
            //这告诉RabbitMQ不要一次向消费者发送多个消息,在消费者处理并确认前一条消息之前，不要向其发送新消息。相反，它会把它发送给下一个不太忙的消费者
            channel.basicQos(1);
            //实现消费方法:
            DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
                /**
                 * 当接收到消息后此方法将被调用
                 * @param consumerTag  消费者标签，用来标识消费者的，在监听队列时设置channel.basicConsume()指定
                 * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志
                (收到消息失败后是否需要重新发送)
                 * @param properties 消息属性
                 * @param body 消息内容
                 * @throws IOException
                 */
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //交换机
                    String exchange = envelope.getExchange();
                    //路由键
                    String routingKey = envelope.getRoutingKey();

                    //消息id，mq在channel中用来标识消息的id，可用于确认消息已接收
                    long deliveryTag = envelope.getDeliveryTag();
                    //消息内容
                    String message = new String(body, "utf-8");
                    if (message.contains("hello")) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            //收到确定消息
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        }
                    }
                    System.out.println("receive message:" + message + "消息id: " + consumerTag);
                }
            };

            //监听队列
            //参数:String queue, boolean autoAck, Consumer callback
            /**
             * 参数明细：
             * 1、queue 队列名称
             * 2、autoAck 自动回复，当消费者接收到消息后要告诉mq消息已接收，如果将此参数设置为tru表示会自动回复mq，如果设置为false要通过编程实现回复
             * 3、callback，消费方法，当消费者接收到消息要执行的方法
             */
            channel.basicConsume(QUEUE, false, defaultConsumer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

}
