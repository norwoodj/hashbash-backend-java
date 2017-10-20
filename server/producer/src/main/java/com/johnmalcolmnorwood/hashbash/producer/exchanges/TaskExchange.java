package com.johnmalcolmnorwood.hashbash.producer.exchanges;

import com.johnmalcolmnorwood.hashbash.mq.Queues;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


public interface TaskExchange {
    @Output(Queues.RAINBOW_TABLE_SEARCH_REQUESTS)
    MessageChannel searchRainbowTable();

    @Output(Queues.RAINBOW_TABLE_GENERATE_REQUESTS)
    MessageChannel generateRainbowTable();
}
