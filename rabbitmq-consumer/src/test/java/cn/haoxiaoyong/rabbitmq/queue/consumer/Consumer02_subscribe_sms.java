package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by haoxy on 2018/12/25.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 发布订阅模式
 * 短信发送
 * 消费者
 */
public class Consumer02_subscribe_sms {

    //队列名称
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

    public static void main(String[] args) throws Exception {
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
        /**
         * 参数明细
         * 1、queue 队列名称
         * 2、durable 是否持久化，如果持久化，mq重启后队列还在
         * 3、exclusive 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列则自动删除,如果将此参数设置true可用于临时队列的创建
         * 4、autoDelete 自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）
         * 5、arguments 参数，可以设置一个队列的扩展参数，比如：可设置存活时间
         */
        channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
        //声明交换机
        //参数: String exchange, String type
        /**
         * 参数明细：
         * exchange 交换机的名称
         * type 交换机的类型
         * fanout：对应的rabbitmq的工作模式是 publish/subscribe (当前例子就是这种模式)
         * direct：对应的Routing	工作模式
         * topic：对应的Topics工作模式
         * headers： 对应的headers工作模式
         */
        channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
        //进行交换机和队列绑定
        //参数: String queue, String exchange, String routingKey
        /**
         * 参数明细
         * queue: 队列名称
         * exchange: 交换机名称
         * routingKey 路由键,作用是交换机根据路由key的值将消息转发到指定的队列中，在发布订阅模式中调协为空字符串
         */

        channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");
        //实现消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            /**
             *当接收到消息后此方法将被调用
             * @param consumerTag 消费者标签，用来标识消费者的，在监听队列时设置channel.basicConsume
             * @param envelope 消息包的内容，可从中获取消息id，消息routingkey，交换机，消息和重传标志
             * @param properties 消息属性
             * @param body 消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //消息id，mq在channel中用来标识消息的id，可用于确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message = new String(body, "utf-8");
                System.out.println(message);

            }
        };
        //监听队列
        //参数:String queue, boolean autoAck, Consumer callback
        /**
         * 参数明细
         * queue: 队列名称
         * autoAck: 自动回复,当消费者接收到消息后要告诉 mq 消息已经接收,如果将此参数设置为tru表示会自动回复mq，如果设置为false要通过编程实现回复
         * callback: 消费方法，当消费者接收到消息要执行的方法
         */
        channel.basicConsume(QUEUE_INFORM_SMS, true, defaultConsumer);

    }

}
