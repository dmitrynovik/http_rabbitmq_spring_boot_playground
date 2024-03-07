# Getting started
1.startup rabbit cluster and create queue and bind exchange
2. update application.yml
> update spring.rabbitmq.addresses to your rabbit address
> 
> update username and password
3. Choose any between two options below:
3a. If you are using `Spring Cloud Streams`:

> 1. create Exchange(topic type): `spring-cloud-stream`
> 2. create queue: `spring-cloud-stream`
> 3. bind the exchange (1) to the queue (2) with the routing key: `spring-cloud-stream`
> 4. In `test.jmx`, set `<stringProp name="HTTPSampler.path">spring-cloud-stream/post</stringProp>`.

3b. If you are using `Spring AMQP`:

> 1. create Exchange(topic type): `spring-amqp`
> 2. create queue: `spring-amqp`
> 3. bind exchange (1) to the queue (2) with the routing key: `stream-amqp`
> 4. In `test.jmx`, set `<stringProp name="HTTPSampler.path">spring-amqp/post</stringProp>`.


4.start SpringBoot app (please make sure your env under JDK 17)
> `cd app/`

> `mvn spring-boot:run`

5. trigger a restful call
> `curl --location --request POST 'http://localhost:9102/dispatcher/nonPartition02'` (or `nonPartition01` if using Spring AMQP).

6. if everything go smooth, you can trigger load test by using this Jmeter script as: `java -jar PATH/TO/JMETER.jar -n -t test.jmx `

# Findings:

1. `SpingAMQP` and `RabbitTemplate` are also doing channel caching, because by default it is using the [CachingConnectionFactory](https://docs.spring.io/spring-amqp/reference/amqp/connections.html#cachingconnectionfactory).

2. `SpingAMQP` and `RabbitTemplate` is the recommnended way as <ins>more performant and less prone to channel leak.</ins>

3. More channels does not mean better performance as all channels are multiplexed over same physical connection (TCP socket).

4. This does not explain a huge increase in the number of RabbitMQ channels as `2047` channels are the connection limit and there is only `1` connection per app.
