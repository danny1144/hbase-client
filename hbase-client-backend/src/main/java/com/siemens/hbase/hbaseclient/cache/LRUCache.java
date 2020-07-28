package com.siemens.hbase.hbaseclient.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 最近最少使用算法缓存
 */
class LRUCache<S, O> extends LinkedHashMap<String, Object> {
    private int capacity;
    public LRUCache(int capacity) {
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }
    public Object get(String key) {
        return super.getOrDefault(key, -1);
    }
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Object> eldest) {
        return size() > capacity;
    }
}

