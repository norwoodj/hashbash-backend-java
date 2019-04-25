package com.johnmalcolmnorwood.hashbash.job.generate.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.johnmalcolmnorwood.hashbash.job.common.listener.RainbowChainChunkListener;
import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.job.common.writer.MappingItemWriter;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Configuration("com.johnmalcolmnorwood.hashbash.job.generate.config.WriterConfig")
public class WriterConfig {

    private static final String RAINBOW_CHAIN_INSERT_SQL =
            "INSERT IGNORE INTO `rainbow_chain` (`startPlaintext`, `endHash`, `rainbowTableId`) " +
                    "VALUES (:startPlaintext, :endHash, :rainbowTableId)";

    private static final Map<String, Function<RainbowChain, Object>> RAINBOW_CHAIN_PROPERTY_MAPPERS = ImmutableMap.of(
            "startPlaintext", RainbowChain::getStartPlaintext,
            "endHash", RainbowChain::getEndHash,
            "rainbowTableId", RainbowChain::getRainbowTableId
    );

    @Autowired
    private DataSource hashbashDatasource;

    @Autowired
    private RainbowTableRepository rainbowTableRepository;

    @Autowired
    private RainbowTableWrapper generateJobRainbowTableWrapper;


    private SqlParameterSource itemSqlParameterSourceProvider(RainbowChain rainbowChain) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        RAINBOW_CHAIN_PROPERTY_MAPPERS.entrySet().stream()
                .forEach(entry -> mapSqlParameterSource.addValue(entry.getKey(), entry.getValue().apply(rainbowChain)));

        return mapSqlParameterSource;
    }

    @Bean(name = "org.springframework.batch.core.ChunkListener-rainbowTable")
    @StepScope
    public ChunkListener rainbowChainWriteListener() {
        return new RainbowChainChunkListener(
                generateJobRainbowTableWrapper.getRainbowTable(),
                rainbowTableRepository
        );
    }

    @Bean(name = "org.springframework.batch.item.ItemWriter-jdbcGenerate")
    public ItemWriter<RainbowChain> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<RainbowChain> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(hashbashDatasource);
        itemWriter.setSql(RAINBOW_CHAIN_INSERT_SQL);
        itemWriter.setItemSqlParameterSourceProvider(this::itemSqlParameterSourceProvider);
        itemWriter.setAssertUpdates(false);

        return itemWriter;
    }

    @Bean(name = "org.springframework.batch.item.ItemWriter-generate")
    public ItemWriter<RainbowChain> rainbowUniquePasswordItemWriter() {
        Function<List<? extends RainbowChain>, List<? extends RainbowChain>> rainbowChainSorter = rainbowChainList -> {
            List<? extends RainbowChain> sortedRainbowChains = Lists.newArrayList(rainbowChainList);
            Collections.sort(sortedRainbowChains);
            return sortedRainbowChains;
        };

        return new MappingItemWriter<>(rainbowChainSorter, jdbcBatchItemWriter());
    }
}
