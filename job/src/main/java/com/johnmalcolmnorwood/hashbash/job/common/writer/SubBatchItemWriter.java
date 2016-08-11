package com.johnmalcolmnorwood.hashbash.job.common.writer;

import com.johnmalcolmnorwood.hashbash.job.common.utils.FunctionUtils;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.function.Function;


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
