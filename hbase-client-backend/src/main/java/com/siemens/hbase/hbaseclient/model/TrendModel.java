package com.siemens.hbase.hbaseclient.model;

import java.util.List;

/**
 * @author jin.liu.ext@siemens.com
 * @date 2019/07/03
 * @description 趋势模型
 */
public class TrendModel {
    /**
     * 不同时间点时间戳集合
     */
    private List<Long> times;
    /**
     * 不同时间点对应值集合
     */
    private List<Double> values;

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }

    public List<Double> getValues() {
        return values;
    }

    public void setValues(List<Double> values) {
        this.values = values;
    }
}
