package com.johnmalcolmnorwood.hashbash.engine.consumers;

import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.engine.exchanges.TaskExchange;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearchStatus;
import com.johnmalcolmnorwood.hashbash.mq.Queues;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableSearchRequestMessage;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamily;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowTableSearchService;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ForkJoinPool;


@EnableBinding(TaskExchange.class)
public class RainbowTableSearchRequestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RainbowTableSearchRequestConsumer.class);

    @Autowired
    private RainbowTableRepository rainbowTableRepository;

    @Autowired
    private RainbowChainRepository rainbowChainRepository;

    @Autowired
    private RainbowTableSearchRepository rainbowTableSearchRepository;

    @Resource(name = "java.util.concurrent.ForkJoinPool-rainbowTableSearch")
    private ForkJoinPool rainbowTableSearchThreadPool;

    private String search(RainbowTable rainbowTable, String hash) {
        ReductionFunctionFamily reductionFunctionFamily = ReductionFunctionFamilies.defaultReductionFunctionFamily(
                rainbowTable.getPasswordLength(),
                rainbowTable.getCharacterSet()
        );

        RainbowChainGeneratorService rainbowChainGeneratorService = new RainbowChainGeneratorService(
                HashFunctions.getHashFunctionByName(rainbowTable.getHashFunction()),
                reductionFunctionFamily
        );

        RainbowTableSearchService rainbowTableSearchService = new RainbowTableSearchService(
                rainbowChainGeneratorService,
                rainbowChainRepository,
                rainbowTable.getId(),
                rainbowTable.getChainLength(),
                rainbowTableSearchThreadPool
        );

        return rainbowTableSearchService.reverseHash(HashCode.fromString(hash));
    }

    @StreamListener(value = Queues.RAINBOW_TABLE_SEARCH_REQUESTS)
    public void rainbowTableSearchRequest(RainbowTableSearchRequestMessage rainbowTableSearchRequestMessage) {
        LOGGER.info(
                "Received Rainbow Table Search Request: {} in rainbow table {}",
                rainbowTableSearchRequestMessage.getHash(),
                rainbowTableSearchRequestMessage.getRainbowTableId()
        );

        rainbowTableSearchRepository.updateStatusAndSearchStartedById(
                rainbowTableSearchRequestMessage.getSearchId(),
                RainbowTableSearchStatus.STARTED,
                Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant())
        );

        var rainbowTable = rainbowTableRepository.findById(rainbowTableSearchRequestMessage.getRainbowTableId());

        if (rainbowTable.isEmpty()) {
            LOGGER.warn("Rainbow Table with ID {} does not exist", rainbowTableSearchRequestMessage.getRainbowTableId());
            throw new RuntimeException("Rainbow Table with that ID doesn't exist");
        }

        String result = search(rainbowTable.get(), rainbowTableSearchRequestMessage.getHash());
        RainbowTableSearchStatus status = (result == null)
                ? RainbowTableSearchStatus.NOT_FOUND
                : RainbowTableSearchStatus.FOUND;

        rainbowTableSearchRepository.updateStatusAndPasswordSearchCompletedById(
                rainbowTableSearchRequestMessage.getSearchId(),
                status,
                result,
                Date.from(ZonedDateTime.now(ZoneId.of("UTC")).toInstant())
        );

        if (result != null) {
            LOGGER.info("Found password for hash {} => {}", rainbowTableSearchRequestMessage.getHash(), result);
        }
    }
}
