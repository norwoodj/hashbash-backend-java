package com.johnmalcolmnorwood.hashbash.job.common.reader;

import org.springframework.batch.item.ItemReader;

import java.util.function.Supplier;

/**
 * This is an ItemReader implementation that will supply items infinitely from a constructor provided supplier
 */
public class SupplierReader<T> implements ItemReader<T> {

    private final Supplier<T> supplier;

    public SupplierReader(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T read() throws Exception {
        return supplier.get();
    }
}
