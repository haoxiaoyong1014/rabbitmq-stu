package cn.haoxiaoyong.rabbitmq.queue.consumer;

import com.rabbitmq.client.*;


/**
 * Created by haoxy on 2018/12/25.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 * 发布订阅模式
 * 生产者
 */
public class Producer02_publish {

    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    //交换机
    private static final String EXCHANGE_FANOUT_INFORM = "exchange_fanout_inform";

    public static void main(String[] args) {
        //通过连接工厂创建新的连接和 mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("haoxy");
        connectionFactory.setPassword("haoxy");
        connectionFactory.setHost("47.100.102.136");
        //设置虚拟机,一个 mq服务可以设置多个虚拟机,每个虚拟机相当于一个独立的 mq
        connectionFactory.setVirtualHost("/");
        Connection connection = null;
        Channel channel = null;
        try {
            //建立新的链接
            connection = connectionFactory.newConnection();
            //创建会话通道,生产者和 mq服务所有的通信都在 channel 通道中完成
            channel = connection.createChannel();
            //声明队列
            //声明两个队列一个用于邮件发送,一个用于短信发送
            //参数: String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            /**
             * 参数明细
             * queue:队列名称
             * durable:是否持久化,如果持久化. mq重启队里还在
             * exclusive: 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列则自动删除,如果将此参数设置true可用于临时队列的创建
             * autoDelete: 自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）
             * arguments:参数，可以设置一个队列的扩展参数，比如：可设置存活时间
             */
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            //声明一个交换机
            //参数: String exchange, String type
            /**
             * 参数明细
             * exchange: 交换机名称
             * type:交换机类型
             * 交换机类型包括:
             *  fanout: 对应的rabbitmq 的工作模式是 publish/subscribe,当前例子就是这种模式
             *  direct: 对应的 Routing 的工作模式
             *  topic: 对应的 Topics工作模式
             *  headers: 对应的 headers工作模式
             */
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
            //交换机和队列进行绑定
            //参数:String queue, String exchange, String routingKey
            /**
             * 参数明细:
             * queue:队列名称
             * exchange: 交换机名称
             * routingKey: 路由 key ,(这个例子中使用不到,下个例子中会用到)
             */
            //同样我们有两个队列需要绑定
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_FANOUT_INFORM, "");

            //发送消息
            //参数: String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * 参数明细
             * exchange:交换机，如果不指定将使用mq的默认交换机（设置为""）
             * routingKey: 路由key，交换机根据路由key来将消息转发到指定的队列，如果使用默认交换机，routingKey设置为队列的名称
             * props: 消息的属性
             * body: 消息内容
             */
            for (int i = 0; i < 5; i++) {
                String message = "send ingorm message to user";

                AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                        .contentType("application/json")
                        .deliveryMode(2)//消息持久化
                        .priority(0)
                        .build();

                channel.basicPublish(EXCHANGE_FANOUT_INFORM, "",properties , message.getBytes());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                channel.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
