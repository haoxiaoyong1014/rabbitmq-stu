
correlationId: 应用程序使用-关联标识符

messageId: 应用程序使用-消息标识符

Timestamp: 应用程序使用-消息时间戳

type: 应用程序使用-消息类型名称

AppId: 应用程序使用-创建应用程序id


首先我们创建了`RabbitMqListenerAware`类并实现了`ChannelAwareMessageListener`和 `ApplicationContextAware`

ChannelAwareMessageListener: 作用主要是接听消息，实现了其中的`onMessage`方法，当生产者成功成功发送消息时这个回调
方法会执行。

ApplicationContextAware： 作用是用来获取容器中的Bean,之后根据反射执行这个Bean中的方法。并实现了其中的`setApplicationContext`方法
用来给application赋值。

我们在RabbitmqConfig中配置了：

```java
 @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(topic_exchange);
        rabbitTemplate.setRoutingKey(routing_message);
        return rabbitTemplate;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory,Queue... queues){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queues);
        MessageListenerAdapter adapter = new MessageListenerAdapter(new RabbitMqListenerAware());
        //设置处理器的消费消息的默认方法,如果没有设置，那么默认的处理器中的默认方式是handleMessage方法
        adapter.setDefaultListenerMethod("onMessage");
        container.setMessageListener(adapter);
        return container;
    }

```

这里主要说一下`MessageListenerAdapter`,我们上面实现的`ChannelAwareMessageListener`接口,同时`MessageListenerAdapter`也实现了该接口

我们在这里设置了消息默认的监听方法`onMessage`所以上面说到的onMessage回调方法才会执行，如果不设置会走默认的`handleMessage`方法

为什么这么说？ 我们看源码可以发现：

```java
public MessageListenerAdapter() {
        this.queueOrTagToMethodName = new HashMap();
        this.defaultListenerMethod = "handleMessage";
        this.delegate = this;
    }
```
所以说如果不设置默认方法会执行`handleMessage`方法。

`BasicProducer`所有生产者的父类,在这里我们将实现发送消息以及消息发送成功和失败的回调,下面贴出发送消息的方法:

```java
public void sendMessage(String serviceName, String serviceMethodName, String correlationId, Object msg) {
        this.rabbitTemplate.setCorrelationKey(correlationId);
        //参数: String routingKey, Object message, MessagePostProcessor messagePostProcessor
        this.rabbitTemplate.convertAndSend(routing_message, msg, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                long tag = new Random().nextLong();
                message.getMessageProperties().setDeliveryTag(tag);
                message.getMessageProperties().setTimestamp(new Date());
                message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
                message.getMessageProperties().setCorrelationId(correlationId);
                message.getMessageProperties().setHeader("serviceName",serviceName);
                message.getMessageProperties().setHeader("serviceMethodName",serviceMethodName);
                System.out.println("Random 随机数:" + tag);
                return message;
            }
        }, new CorrelationData(correlationId));

    }
```

当然我们也是调用`rabbitTemplate`进行发送消息,其中参数有: 路由键，消息内容以及消息属性,在消息属性中我们的重点是:

`message.getMessageProperties().setHeader("serviceName",serviceName);`

` message.getMessageProperties().setHeader("serviceMethodName",serviceMethodName);`

`serviceName`的值是消费者的类名, `serviceMethodName`:消费者的方法名

这个消息发送成功之后就会执行上面说到的消息监听方法`onMessage`方法，在其中我们通过反射执行消息者方法。

消费者接收到消息,处理完成之后并ack