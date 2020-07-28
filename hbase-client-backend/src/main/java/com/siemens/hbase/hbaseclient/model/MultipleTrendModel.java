package com.siemens.hbase.hbaseclient.model;

import java.util.List;
import java.util.Map;

/**
 * @date 2019/11/29
 * @description 多列趋势模型
 */
public class MultipleTrendModel extends MultipleValueModel{
    /**
     * 不同时间点时间戳集合
     */
    private List<Long> times;
    /**
     * 测点单位map
     */
    private Map<String,String> uomMap;

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public Map<String, String> getUomMap() {
        return uomMap;
    }

    public void setUomMap(Map<String, String> uomMap) {
        this.uomMap = uomMap;
    }
}
