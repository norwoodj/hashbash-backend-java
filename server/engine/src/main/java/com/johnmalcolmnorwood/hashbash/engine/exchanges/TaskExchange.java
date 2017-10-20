package com.johnmalcolmnorwood.hashbash.engine.exchanges;

import com.johnmalcolmnorwood.hashbash.mq.Queues;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;


public interface TaskExchange {
    @Input(Queues.RAINBOW_TABLE_SEARCH_REQUESTS)
    SubscribableChannel searchRainbowTable();

    @Input(Queues.RAINBOW_TABLE_GENERATE_REQUESTS)
    SubscribableChannel generateRainbowTable();
}
