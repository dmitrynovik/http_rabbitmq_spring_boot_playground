package demo.producer.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@RestController()
@Slf4j
@RequestMapping("/stream-bridge")
public class SpringCloudStreamsDispatcherController {
    @Autowired
    private StreamBridge streamBridge;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    @PostMapping("post")
    public boolean dispatcherNonPartitionMessage02() {
            String value = data[RANDOM.nextInt(data.length)];
            log.info("Sending: " + value);

            CorrelationData correlationData = new CorrelationData();
            CompletableFuture<CorrelationData.Confirm> future = correlationData.getFuture();
            future.whenComplete((r, error) -> {
                if (error != null) {
                    MDC.put("dataType", value);
                    log.error("fail to publish {} {}", value, value);
                } else {
                    if (!r.isAck()) {
                        MDC.put("dataType", value);
                        log.error("fail to ack {}, reason:{}, {}", value, r.getReason(), value);
                        MDC.clear();
                    }
                    if (correlationData.getReturned() != null) {
                        MDC.put("dataType", value);
                        log.error("fail to send to EVDP internal queue {} {} {}", correlationData.getReturned().getReplyText(), value, value);
                        MDC.clear();
                    }
                }
            });

            Message<String> message = MessageBuilder.withPayload(value)
                    .setHeader(AmqpHeaders.PUBLISH_CONFIRM_CORRELATION, correlationData)
                    .setHeader("bookingNum", value.length())
                    .build();

            streamBridge.send("stream-bridge", message);
            
        return true;
    }

    private static final String[] data = new String[]{
            "f", "g", "h", //making them go to partition-0 by making a single char string
            "fo", "go", "ho",
            "foo", "goo", "hoo",
            "fooz", "gooz", "hooz",
            "foooz", "gooz", "hooz",
            "fooooz", "fooooz", "fooooz",
            "foooooz", "foooooz", "foooooz",
            "fooooooz", "fooooooz", "fooooooz",
            "foooooooz", "foooooooz", "foooooooz",
            "fooooooooz", "fooooooooz", "fooooooooz",
            "foooooooooz", "foooooooooz", "foooooooooz",
            "fooooooooooz", "fooooooooooz", "fooooooooooz",
            "foooooooooooz", "foooooooooooz", "foooooooooooz",
            "fooooooooooooz", "fooooooooooooz", "fooooooooooooz",
            "foooooooooooooz", "foooooooooooooz", "foooooooooooooz",
    };
}
