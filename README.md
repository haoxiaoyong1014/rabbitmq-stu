##### 重新认识RabbitMQ 

**rabbitmq-stu**

**入门程序**

#### Work queues 工作模式

<img src="http://www.rabbitmq.com/img/tutorials/python-two.png" height="110">

消费者：  <a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-consumer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Consumer01.java">Consumer01</a>

消费者：  <a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-consumer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Consumer01_1.java">Consumer01_1</a>

生产者： <a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-producer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Producer01.java">Producer01</a>

* Work queues 工作模式: 两个消费者或者多个消费者共同消费同一个队列中的消息
 
* 应用场景: 对于任务过重或任务较多情况使用工作队列可以提高任务处理的速度。

* 测试
    
    * 1, 使用上面的案例,启动多个消费者.
    
    * 2, 生产者发送多个消息,
    
* 结果

    *  1,一条消息只会被一个消费者接收
    
    *  2, rabbit采用轮询的方式将消息平均发送给消费者的
    
    *  3, 消费者在处理完某条消息后,才会收到下一条消息  
    
      
    
#### 消费者丢失消息
    
其中我们使用了手动确定消息的方式(手动 ack), 

`channel.basicConsume(QUEUE, false, defaultConsumer);`

将第二个参数设置为 false,然后在消息的回调方法中加上:

`channel.basicAck(envelope.getDeliveryTag(), false);`

其中`envelope.getDeliveryTag()` 是这个消息的id,要告诉 rabbitmq 哪个消息处理完了,(代码中有详细的注释介绍) 经过测试发现设置成手动ack之后,就算是消费者在消费这个消息的时候挂掉, 
rabbitmq会认为你这个消息没有消费,因为 rabbit没有收到消息确定的回复了. 如果设置成自动回复,大家都知道当消费者取到这个消息的时候, RabbitMQ 就会收到 ack 的
确定回复,一旦RabbitMQ将消息传递给消费者，它立即将其标记为删除,RabbitMQ会认为你已经处理完了,重启刚刚挂掉的消费者也无济于事.

#### 消息持久化

上面我们学会了即使消费者死亡,任务也不会丢失,但是如果 RabbitMQ 服务器停止,我们的任务仍然会丢失。
当RabbitMQ退出或崩溃时，它将忘记队列和消息,确保消息不会丢失需要做两件事：

**一,我们需要将队列标记为持久**


在消费者中 `channel.queueDeclare("helloworld", true, false, false, null);`

参数: String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments

参数细节: 
    
     queue : 队列名称
     durable: 是否持久化，如果持久化，mq重启后队列还在
     exclusive: 是否独占连接，队列只允许在该连接中访问，如果connection连接关闭队列则自动删除,如果将此参数设置true可用于临时队列的创建
     autoDelete: 自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列（队列不用了就自动删除）   
     arguments: 参数，可以设置一个队列的扩展参数，比如：可设置存活时间                                      

代码中有更多的参数细节解释
  
我们将durable=true 即使RabbitMQ重新启动, helloworld 队列也不会丢失    

**二,下面我们要将消息标记持久化**

在生产者中 `channel.basicPublish("", QUEUE, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());`

参数: String exchange, String routingKey, BasicProperties props, byte[] body

参数细节:
    
    exchange: 交换机,如果不指定将使用 mq 默认的交换机(设置为: ""),下个例子会用到交换机
    routingKey: 路由键,交换机根据路由键来将消息转发到指定的队列,如果使用默认交换机，routingKey设置为队列的名称
    props: 消息的属性
    body: 消息内容
    
通过将MessageProperties（实现BasicProperties）设置为值PERSISTENT_TEXT_PLAIN。这样我们就将消息设置为持久化了.    

#### publish/subscribe工作模式 (又称发布订阅模式)

<img src="http://www.rabbitmq.com/img/tutorials/exchanges.png" height="110">

上一个案例中我们演示了`消费者丢失消息`和 `消息持久化`,这次为了简化代码使用自动ack,

消费者：  <a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-consumer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Consumer02_subscribe_email.java">Consumer02_subscribe_email</a>

消费者：  <a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-consumer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Consumer02_subscribe_sms.java">Consumer02_subscribe_sms</a>

生产者： <a href="https://gitlab.com/haoxiaoyong/rabbitmq-stu/blob/master/rabbitmq-producer/src/test/java/cn/haoxiaoyong/rabbitmq/consumer/Producer02_publish.java">Producer02_publish</a>

* publish/subscribe模式：

       向多个消费者中传递消息，此模式也称发布/订阅
       
       每个消费者监听自己的队列。
       
       生产者将消息发给broker(mq)，由交换机将消息转发到绑定此交换机的每个队列，每个绑定交换机的队列都将接收到消息
       
      

* 应用场景：开发中一边需要发送短信，一边需要发送邮件。

**在此模式中我们将引入一个新的概念-Exchanges(交换机)**

生产者不是将消息直接发送到队列，而是发送到交换机，由交换机将消息转发到绑定此交换机的每个队列

RabbitMQ中消息传递模型的核心思想是生产者永远不会将任何消息直接发送到队列。甚至生产者通常不知道消息是否会被传递到任何队列。

* 交换机只做两件事情：
    
      接收来自生产者的消息
      
      将它们推送到队列中
      
* 交换机有一下几种类型可供选择：
            
            direct: 对应的 Routing 的工作模式
            
            topic: 对应的 Topics工作模式
            
            headers: 对应的 headers工作模式
            
            fanout：对应的rabbitmq 的工作模式是 publish/subscribe,(也是本案例中的类型)
     
* 下面是官网对fanout的解释

> The fanout exchange is very simple. As you can probably guess from the name, it just broadcasts all the messages it receives to all the queues it knows. And that's exactly what we need for our logger.
   
大致意思就是:这个fanout交换机模式贼简单，他只会把这个消息讲给他认识的人听(它只是将收到的所有消息广播到它知道的所有队列中)；

大家看看代码也就很清楚fanout交换机模式的意思了，这里就不过的的去讲述，在这里请大家思考几个问题，
               
* 1、publish/subscribe与work queues有什么区别以及相同点？
            
* 2，实质工作用什么 publish/subscribe还是work queues？

* 3，在上个案例中(work queues)没有提到交换机，为什么也能生产和消费？


