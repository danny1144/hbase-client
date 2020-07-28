package com.siemens.hbase.hbaseclient.model;

import java.util.List;
import java.util.Map;

/**
 * @author jin.liu@siemens.com
 * @date 2020/03/17
 * @description 多列指标值模型
 */
public class MultipleValueModel {
    /**
    * 多个指标不同时间点对应值集合
    */
    private Map<String,List<Double>> maps;

    public Map<String, List<Double>> getMaps() {
        return maps;
    }

    public void setMaps(Map<String, List<Double>> maps) {
        this.maps = maps;
    }

}
