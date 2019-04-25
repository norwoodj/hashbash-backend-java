package com.johnmalcolmnorwood.hashbash.producer.exchanges;

import com.johnmalcolmnorwood.hashbash.mq.Queues;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;


public interface TaskExchange {
    @Output(Queues.RAINBOW_TABLE_DELETE_REQUESTS)
    MessageChannel deleteRainbowTable();

    @Output(Queues.RAINBOW_TABLE_GENERATE_REQUESTS)
    MessageChannel generateRainbowTable();

    @Output(Queues.RAINBOW_TABLE_SEARCH_REQUESTS)
    MessageChannel searchRainbowTable();

}
