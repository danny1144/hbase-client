package com.siemens.hbase.hbaseclient.cache;

import java.util.Map;

/**
 * @Description: 本地缓存
 * @author: zhongxp
 * @Date: 7/22/2020 1:12 PM
 */
public class SingletonMap {

    //一个本地的缓存Map
    private Map<String, Object> localCacheStore = new LRUCache<String, Object>(100);
    //一个私有的对象，非懒汉模式
    private static SingletonMap singletonMap = new SingletonMap();
    //私有构造方法，外部不可以new一个对象
    private SingletonMap() {
    }
    //静态方法，外部获得实例对象
    public static SingletonMap getInstance() {
        return singletonMap;
    }
    //获得缓存中的数据
    public Object getValueByKey(String key) {
        return localCacheStore.get(key);
    }
    //向缓存中添加数据
    public void putValue(String key, Object value) {
        localCacheStore.put(key, value);
    }
}
