package com.siemens.hbase.hbaseclient.model;

/**
 * @date 2019/06/04
 * @description Hbase过滤条件实体
 */
public class DoubleFilterModel {

    /**
     * hbase列名称
     * */
    private String name;
    /**
     * 上线值
     * */
    private Double top;
    /**
     * 下线值
     * */
    private Double bottom;

    public DoubleFilterModel(String name, Double top, Double bottom) {
        this.name = name;
        this.top = top;
        this.bottom = bottom;
    }
    public DoubleFilterModel(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTop() {
        return top;
    }

    public void setTop(Double top) {
        this.top = top;
    }

    public Double getBottom() {
        return bottom;
    }

    public void setBottom(Double bottom) {
        this.bottom = bottom;
    }
}
