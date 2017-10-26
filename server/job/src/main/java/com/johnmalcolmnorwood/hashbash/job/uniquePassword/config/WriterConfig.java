package com.johnmalcolmnorwood.hashbash.job.uniquePassword.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.job.common.writer.MappingItemWriter;
import com.johnmalcolmnorwood.hashbash.job.common.writer.SubBatchItemWriter;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableUniquePassword;
import com.johnmalcolmnorwood.hashbash.rainbow.model.RainbowChainLink;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Configuration("com.johnmalcolmnorwood.hashbash.job.uniquePassword.config.WriterConfig")
public class WriterConfig {

    private static final String RAINBOW_UNIQUE_PASSWORD_INSERT_SQL =
            "INSERT IGNORE INTO `rainbow_table_unique_password` (`password`, `rainbowTableId`) " +
                    "VALUES (:password, :rainbowTableId)";

    private static final Map<String, Function<RainbowTableUniquePassword, Object>> RAINBOW_CHAIN_PROPERTY_MAPPERS = ImmutableMap.of(
            "password", RainbowTableUniquePassword::getPassword,
            "rainbowTableId", rainbowTableUniquePassword -> rainbowTableUniquePassword.getRainbowTable().getId()
    );

    @Autowired
    private DataSource hashbashDatasource;

    @Autowired
    private RainbowTableWrapper rainbowTableWrapper;


    private SqlParameterSource itemSqlParameterSourceProvider(RainbowTableUniquePassword rainbowTableUniquePassword) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        RAINBOW_CHAIN_PROPERTY_MAPPERS.entrySet().stream()
                .forEach(entry -> mapSqlParameterSource.addValue(entry.getKey(), entry.getValue().apply(rainbowTableUniquePassword)));

        return mapSqlParameterSource;
    }

    @Bean(name = "org.springframework.batch.item.ItemWriter-jdbcUniquePassword")
    public ItemWriter<RainbowTableUniquePassword> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<RainbowTableUniquePassword> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(hashbashDatasource);
        itemWriter.setSql(RAINBOW_UNIQUE_PASSWORD_INSERT_SQL);
        itemWriter.setItemSqlParameterSourceProvider(this::itemSqlParameterSourceProvider);
        itemWriter.setAssertUpdates(false);

        return itemWriter;
    }

    @Bean(name = "org.springframework.batch.item.ItemWriter-uniquePassword")
    @StepScope
    public ItemWriter<List<RainbowChainLink>> rainbowChainItemWriter() {
        Function<List<? extends RainbowChainLink>, List<? extends RainbowTableUniquePassword>> rainbowPasswordSorter =
                rainbowChainLinkList -> rainbowChainLinkList.stream()
                        .map(RainbowChainLink::getPlaintext)
                        .sorted()
                        .map(password -> RainbowTableUniquePassword.builder()
                                .password(password)
                                .rainbowTable(rainbowTableWrapper.getRainbowTable())
                                .build()
                        )
                        .collect(Collectors.toList());

        // This ItemWriter receives a list of chain links and maps it to a database object, sorts them by the passwords
        // to avoid deadlocks, and delegates the list of sorted database objects to the jdbcBatchItemWriter
        ItemWriter<RainbowChainLink> chainLinkToPasswordMappingItemWriter = new MappingItemWriter<>(
                rainbowPasswordSorter,
                jdbcBatchItemWriter()
        );

        // This ItemWriter takes a list of lists of chain links and for each list of chain links, delegates
        // it to the writer above
        ItemWriter<List<RainbowChainLink>> chainLinkSubBatchWriter = new SubBatchItemWriter<>(
                Function.identity(),
                chainLinkToPasswordMappingItemWriter
        );

        // This ItemWriter takes a list of lists of chain links and for each list of chain links, partitions that
        // list into a list of lists of chain links of length 1000, and delegates each of those lists of smaller lists
        // to the writer above
        return new SubBatchItemWriter<>(
                rainbowChainLinkLists -> Lists.partition(rainbowChainLinkLists, 1000),
                chainLinkSubBatchWriter
        );
    }
}
