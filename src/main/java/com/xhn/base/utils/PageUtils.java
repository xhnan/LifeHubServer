package com.xhn.base.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;

import java.util.List;

/**
 * @author xhn
 * @date 2026/1/4 16:34
 * @description
 */
public class PageUtils<T,R> implements IPage<T> {

    /**
     * 总记录数
     */
    private Long total;

    private Long current;

    private Long size;

    private List<OrderItem> orders;

    private List<R> searchParams;

    private List<T> records;


    @Override
    public List<OrderItem> orders() {
        return this.orders;
    }

    @Override
    public List<T> getRecords() {
        return List.of();
    }

    @Override
    public IPage<T> setRecords(List<T> records) {
        return null;
    }

    @Override
    public long getTotal() {
        return 0;
    }

    @Override
    public IPage<T> setTotal(long total) {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public IPage<T> setSize(long size) {
        return null;
    }

    @Override
    public long getCurrent() {
        return 0;
    }

    @Override
    public IPage<T> setCurrent(long current) {
        return null;
    }
}
