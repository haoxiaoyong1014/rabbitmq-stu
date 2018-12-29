#### rabbitmq-springboot

**rabbitmq集成springboot**

1,使用 topic(通配符模式),在 `RabbitmqConfig`配置文件中进行了`声明队列`,  `声明交换机`,`交换机和队列进行绑定`,其中交换机和队列进行了绑定,

例如: 邮件队列`queue_inform_email`绑定的路由key是`inform.#.email.#`,在生产者发送消息的时候使用的路由key是`inform.abc.email.abc`,

刚好这个路由key匹配到了`inform.#.email.#`(邮件队列绑定的刚好是这个路由key),自然这个消息就发送到了邮件接收的那个消费者(监听邮件队列的消费者)

#### 发布确定

2, 在`CallbackConfig`文件中,我们实现了`ConfirmCallback`,和`ReturnCallback`接口,分别实现其中的`confirm`,`returnedMessage`方法

其中`confirm`是`ConfirmCallback`中的方法,主要作用是: 通过实现 ConfirmCallback 接口，消息发送到 Broker 后触发回调，确认消息是否到达 Broker 服务器，也就是只确认是否正确到达 Exchange 中.

```java
@Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("消息的唯一标识:" + correlationData);
        System.out.println("确定结果:" + ack);
        System.out.println("失败原因:" + cause);
    }
```
如果没有找到交换机

```java
消息的唯一标识: CorrelationData [id=testProducer_all]
确定结果: false
失败原因: channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'exchange_topics_inform' in vhost '/', class-id=60, method-id=40)
```
如果找到交换机(表示消息成功到底交换机)

```java
消息的唯一标识: CorrelationData [id=testProducer_all]
确定结果: true
失败原因: null
```

其中`returnedMessage`是`ReturnCallback`中的方法,主要作用: 通过实现 ReturnCallback 接口，启动消息失败返回(只有消息发送失败才会执行)，比如路由不到队列时触发回调

要实现这个发布确定的功能当然只实现这两个接口是不行的我们需要再配置文件中配置

```yml
spring:
  rabbitmq:
    publisher-confirms: true 
    publisher-returns: true 

```
#### 消息接收确定

* 消息通过 ACK 确认是否被正确接收，每个 Message 都要被确认（acknowledged），可以手动去 ACK 或自动 ACK

* 自动确认会在消息发送给消费者后立即确认，但存在丢失消息的可能，如果消费端消费逻辑抛出异常，也就是消费端没有处理成功这条消息，那么就相当于丢失了消息

* 如果消息已经被处理，但后续代码抛出异常，使用 Spring 进行管理的话消费端业务逻辑会进行回滚，这也同样造成了实际意义的消息丢失

* 如果手动确认则当消费者调用 ack、nack、reject 几种方法进行确认，手动确认可以在业务失败后进行一些操作，如果消息未被 ACK 则会发送到下一个消费者

* 如果某个服务忘记 ACK 了，则 RabbitMQ 不会再发送数据给它，因为 RabbitMQ 认为该服务的处理能力有限

* ACK 机制还可以起到限流作用，比如在接收到某条消息时休眠几秒钟

* 消息确认模式有：

           AcknowledgeMode.NONE：自动确认
           AcknowledgeMode.AUTO：根据情况确认
           AcknowledgeMode.MANUAL：手动确认

**确定消息** 

* 默认情况下消息消费者是自动 ack （确认）消息的，如果要手动 ack（确认）则需要修改确认模式为 manual  

```yml
spring:
  rabbitmq:
    listener:
      simple:
        acknowledge-mode: manual

```

#### 确定消息(局部方法处理消息)

```java
   @RabbitHandler
    @RabbitListener(queues = {"queue_inform_email"})//inform.#.email.#
    public void receiverEmail(String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        //接收到消息
        System.out.println("email接收到消息" + msg);
        //处理业务逻辑.....
        try {
            //消息确定
            channel.basicAck(tag, false);
            System.out.println("email_消息ID:" + tag + "已经消费完毕..."); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
```

* 需要注意的 basicAck 方法需要传递两个参数

    * deliveryTag（唯一标识 ID）：当一个消费者向 RabbitMQ 注册后，会建立起一个 Channel ，RabbitMQ 会用 basic.deliver 方法向消费者推送消息，这个方法携带了一个 delivery tag， 它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID，是一个单调递增的正整数，delivery tag 的范围仅限于 Channel,同时告诉 rabbitmq哪个消息确定了
      
    * multiple：为了减少网络流量，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 delivery_tag 小于等于传入值的所有消息
      
这时如果在处理业务逻辑时出错了,也就没有确定消息,RabbitMq 会认为你这个消息没有处理完成,会重新分配下一个消费者(当你开启多个消费者的时候)    

