package com.johnmalcolmnorwood.hashbash.mq;

/**
 * The names of the queues used for async action in this project
 */
public interface Queues {
    String RAINBOW_TABLE_DELETE_REQUESTS = "deleteRainbowTable";
    String RAINBOW_TABLE_GENERATE_REQUESTS = "generateRainbowTable";
    String RAINBOW_TABLE_SEARCH_REQUESTS = "searchRainbowTable";
}
