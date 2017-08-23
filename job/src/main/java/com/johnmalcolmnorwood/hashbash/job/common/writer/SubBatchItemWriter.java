package com.johnmalcolmnorwood.hashbash.job.common.writer;

import com.johnmalcolmnorwood.hashbash.job.common.utils.FunctionUtils;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.function.Function;


/**
 * This ItemWriter receives a list of items of type T, and then splits each of these items into a list of items of type S
 * and passes each of these lists to the delegate item writer
 *
 * @param <T> The type of item that are passed to this item writer
 * @param <S> The type of item that each item of type T is split into a list of, and then delegated to the delegate item writer
 */
public class SubBatchItemWriter<T, S> implements ItemWriter<T> {

    private final Function<T, List<S>> subBatchMapper;
    private final ItemWriter<S> subBatchItemWriter;

    public SubBatchItemWriter(
            Function<T, List<S>> subBatchMapper,
            ItemWriter<S> subBatchItemWriter
    ) {
        this.subBatchMapper = subBatchMapper;
        this.subBatchItemWriter = subBatchItemWriter;
    }

    @Override
    public void write(List<? extends T> items) throws Exception {
        items.stream()
                .map(subBatchMapper)
                .forEach(FunctionUtils.propagateExceptions(subBatchItemWriter::write));
    }
}
