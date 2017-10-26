package com.johnmalcolmnorwood.hashbash.job.common.writer;

import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.function.Function;

/**
 * This class will apply a mapping function to the list of items before delegating to another item writer
 *
 * @param <T> The type of items passed to this item writer
 * @param <R> The type the items are transformed into before being passed into the delegate item writer
 */
public class MappingItemWriter<T, R> implements ItemWriter<T> {

    private final Function<List<? extends T>, List<? extends R>> mapper;
    private final ItemWriter<R> delegateItemWriter;

    public MappingItemWriter(
            Function<List<? extends T>, List<? extends R>> mapper,
            ItemWriter<R> delegateItemWriter
    ) {
        this.mapper = mapper;
        this.delegateItemWriter = delegateItemWriter;
    }

    @Override
    public void write(List<? extends T> list) throws Exception {
        List<? extends R> mappedItemList = mapper.apply(list);
        delegateItemWriter.write(mappedItemList);
    }
}
