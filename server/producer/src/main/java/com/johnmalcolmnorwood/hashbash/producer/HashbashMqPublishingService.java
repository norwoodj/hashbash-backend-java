package com.johnmalcolmnorwood.hashbash.producer;

import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableActionRequestMessage;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableSearchRequestMessage;
import com.johnmalcolmnorwood.hashbash.producer.exchanges.TaskExchange;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;

@EnableBinding(TaskExchange.class)
public class HashbashMqPublishingService {

    @Resource(name = "deleteRainbowTable")
    private MessageChannel deleteRainbowTableChannel;

    @Resource(name = "generateRainbowTable")
    private MessageChannel generateRainbowTableChannel;

    @Resource(name = "searchRainbowTable")
    private MessageChannel searchRainbowTableChannel;

    private static <M> void sendMessage(MessageChannel messageChannel, M msg) {
        messageChannel.send(MessageBuilder.withPayload(msg).build());
    }

    public void sendRainbowTableDeleteRequestMessage(RainbowTableActionRequestMessage msg) {
        sendMessage(deleteRainbowTableChannel, msg);
    }

    public void sendRainbowTableGenerateRequestMessage(RainbowTableActionRequestMessage msg) {
        sendMessage(generateRainbowTableChannel, msg);
    }

    public void sendRainbowTableSearchRequestMessage(RainbowTableSearchRequestMessage msg) {
        sendMessage(searchRainbowTableChannel, msg);
    }
}
