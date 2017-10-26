package com.johnmalcolmnorwood.hashbash.engine.exchanges;

import com.johnmalcolmnorwood.hashbash.mq.Queues;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;


public interface TaskExchange {
    @Input(Queues.RAINBOW_TABLE_DELETE_REQUESTS)
    SubscribableChannel deleteRainbowTable();

    @Input(Queues.RAINBOW_TABLE_GENERATE_REQUESTS)
    SubscribableChannel generateRainbowTable();

    @Input(Queues.RAINBOW_TABLE_SEARCH_REQUESTS)
    SubscribableChannel searchRainbowTable();
}
