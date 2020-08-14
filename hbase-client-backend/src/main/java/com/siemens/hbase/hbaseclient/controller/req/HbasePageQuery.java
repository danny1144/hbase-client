package com.siemens.hbase.hbaseclient.controller.req;

import lombok.Data;

/**
 * @Description: Hbase分页查询
 * @author: zhongxp
 * @Date: 7/21/2020 11:04 AM
 */
@Data
public class HbasePageQuery {


    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long endTime;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 行键前缀值
     */
    private String rowKeyPrefix;
    /**
     * 开始rowkey
     */
    private String startRowKey;
    /**
     * 结束rowkey
     */
    private String endRowKey;

    /**
     * 列簇
     */
    private String columnFamily;
    /**
     * 列
     */
    private String column;
    /**
     * 容量
     */
    private int pageSize;
    /**
     * 页码
     */
    private int currentPage;
}
