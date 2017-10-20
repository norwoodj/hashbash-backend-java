package com.johnmalcolmnorwood.hashbash.producer;

import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableGenerateRequestMessage;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableSearchRequestMessage;
import com.johnmalcolmnorwood.hashbash.producer.exchanges.TaskExchange;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

@EnableBinding(TaskExchange.class)
public class HashbashMqPublishingService {

    @Resource(name = "searchRainbowTable")
    private MessageChannel searchRainbowTableChannel;

    @Resource(name = "generateRainbowTable")
    private MessageChannel generateRainbowTableChannel;

    public void sendRainbowTableSearchRequestMessage(RainbowTableSearchRequestMessage msg) {
        searchRainbowTableChannel.send(MessageBuilder.withPayload(msg).build());
    }

    public void sendRainbowTableGenerateRequestMessage(RainbowTableGenerateRequestMessage msg) {
        generateRainbowTableChannel.send(MessageBuilder.withPayload(msg).build());
    }
}
