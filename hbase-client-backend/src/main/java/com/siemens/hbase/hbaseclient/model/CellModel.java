package com.siemens.hbase.hbaseclient.model;

import java.io.Serializable;

/**
 * @author zxp@siemens.com
 * @date 2019/04/04
 * @description HBase单元格实体
 */
public class CellModel implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 行键值
     * */
    String rowKey;
    /**
     * 列簇
     * */
    String columnFamily;
    /**
     * 列
     * */
    String column;
    /**
     * 值
     * */
    Object value;
    /**
     * 版本时间戳
     * */
    Long timestamp;

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
