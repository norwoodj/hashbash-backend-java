package com.johnmalcolmnorwood.hashbash.engine.consumers;

import com.johnmalcolmnorwood.hashbash.engine.exchanges.TaskExchange;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.mq.Queues;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableActionRequestMessage;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;


@EnableBinding(TaskExchange.class)
public class RainbowTableDeleteRequestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RainbowTableDeleteRequestConsumer.class);

    @Autowired
    private RainbowTableRepository rainbowTableRepository;

    @StreamListener(value = Queues.RAINBOW_TABLE_DELETE_REQUESTS)
    public void rainbowTableDeleteRequest(RainbowTableActionRequestMessage rainbowTableDeleteRequestMessage) {
        LOGGER.info(
                "Received Rainbow Table Delete Request for rainbow table {}",
                rainbowTableDeleteRequestMessage.getRainbowTableId()
        );

        RainbowTable rainbowTable = rainbowTableRepository.findOne(rainbowTableDeleteRequestMessage.getRainbowTableId());

        if (rainbowTable == null) {
            LOGGER.warn("No Rainbow Table with ID {}", rainbowTableDeleteRequestMessage.getRainbowTableId());
            throw new RuntimeException(String.format("No Rainbow Table with ID %s", rainbowTableDeleteRequestMessage.getRainbowTableId()));
        }

        LOGGER.info("Deleting Rainbow Table {} and {} chains", rainbowTable.getId(), rainbowTable.getNumChains());
        rainbowTableRepository.delete(rainbowTable.getId());
    }
}
