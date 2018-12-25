#### 重新认识RabbitMQ - 入门案例

**rabbitmq-producer**

**生产者**

#### Work queues 工作模式


<a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-producer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Producer01.java">Producer01</a>

* 创建连接

* 创建通道

* 声明队列

* 发送消息

```java
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
             * props: 息的属性
             * body: 消息内容
             */
            //消息内容
            String message = "hello world haoxiaoyong";
            channel.basicPublish("", QUEUE, null, message.getBytes());

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

```
#### publish/subscribe工作模式 (又称发布订阅模式)


<a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-producer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Producer02_publish.java">Producer02_publish</a>



* 建立链接

* 创建通道

* 声明队列(多个队列)

* 声明交换机

* 交换机和队列进行绑定(多个)

* 发送消息

在这种模式下，`声明队列`和`交换机和队列进行绑定`可以省略

```java
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
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setHost("www.haoxiaoyong.cn");
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
                channel.basicPublish(EXCHANGE_FANOUT_INFORM, "", null, message.getBytes());

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

```


