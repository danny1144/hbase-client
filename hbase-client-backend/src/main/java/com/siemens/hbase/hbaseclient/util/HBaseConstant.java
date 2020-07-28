package com.siemens.hbase.hbaseclient.util;

/**
 * @author jin.liu@siemens.com
 * @date 2019/11/01
 * @description hbase参数常量
 */
public final class HBaseConstant {

    private HBaseConstant() {}
    public static final String DATA_SET = "dataSet";
    /**
     * 性能特性计算表名称
     */
    public static final String PERFORMANCE_TABLE_NAME = "pma_performance";

    /**
     * 偏差分析表名称
     */
    public static final String DEVIATION_TABLE_NAME = "pma_deviation";

    /**
     * 压气机水洗表名称
     */
    public static final String COMPRESSOR_TABLE_NAME = "pma_compressor";

    /**
     * 列簇名称
     */
    public static final String FAMILY_COLUMN_NAME = "result";

    /**
     * 计算状态结果字段
     */
    public static final String STATUS_COLUMN_NAME = "status";

    /**
     * 偏差分析工况名称
     */
    public static final String POINT_COLUMN_NAME = "point";

    /**
     * hbase rowKey格式字符串
     */
    public static final String ROW_KEY_FORMAT = "%s:%s";

}
