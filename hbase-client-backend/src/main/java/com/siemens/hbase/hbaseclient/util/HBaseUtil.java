package com.siemens.hbase.hbaseclient.util;

import com.siemens.hbase.hbaseclient.model.CellModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.MD5Hash;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @date 2019/05/26
 * @description HBase操作工具类
 */
public final class HBaseUtil {

    private static Map<Integer,Integer> dayMap = getInternalDayMap();

    private HBaseUtil() {}

    /**
     * 获取指定值对应数据类型的字节数组
     * @param value 需要转换字节数组的值
     * @return
     */
    public static byte[] getBytes(Object value) {
        byte[] valueBytes;
        if(value == null){
            valueBytes = Bytes.toBytes(BaseConstant.EMPTY_STRING);
        } else if (String.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((String)value);
        } else if (Float.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((Float)value);
        } else if (Double.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((Double)value);
        }  else if (Short.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((Short)value);
        } else if (Integer.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((Integer)value);
        } else if (Long.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((Long)value);
        } else if (Boolean.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((Boolean)value);
        } else if (ByteBuffer.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((ByteBuffer)value);
        } else if (BigDecimal.class.isInstance(value)) {
            valueBytes = Bytes.toBytes((BigDecimal)value);
        } else {
            valueBytes = Bytes.toBytes(value.toString());
        }
        return valueBytes;
    }

    /**
     * 通过没有hash前缀字符和时间戳反转得到rowkey字符串
     * @param prefix 需要hash的前缀字符串
     * @param timestamp 时间戳
     * @return 处理后的rowkey
     */
    public static String getRowKeyByPrefix(String prefix,long timestamp) {
        String hashPrefix = getMD5HashString(prefix);
        long reverseTime = Long.MAX_VALUE - timestamp;
        return String.format(BaseConstant.UNDERLINE_CHARACTER,hashPrefix,reverseTime);
    }

    /**
     * 通过hash前缀字符和时间戳反转得到rowkey字符串
     * @param hashPrefix hash前缀字符串
     * @param timestamp 时间戳
     * @return 处理后的rowkey
     */
    public static String getRowKeyByHashPrefix(String hashPrefix,long timestamp) {
        long reverseTime = Long.MAX_VALUE - timestamp;
        return String.format(BaseConstant.UNDERLINE_CHARACTER,hashPrefix,reverseTime);
    }

    /**
     * 获取指定字符串的MD5Hash字符串
     * @param prefix 需要hash的字符串
     * @return String
     */
    public static String getMD5HashString(String prefix) {
        return MD5Hash.getMD5AsHex(Bytes.toBytes(prefix));
    }

    /**
     * 获取Hbase对应rowKey中时间戳
     * @param rowKey 行键值
     * @return Long 时间戳
     */
    public static Long getTimestampInRowKey(String rowKey) {
        if (rowKey != null && rowKey.contains(BaseConstant.UNDERLINE_STRING)) {
            String subString = rowKey.substring(rowKey.indexOf(BaseConstant.UNDERLINE_STRING) + 1);
            if (StringUtils.isNumeric(subString)) {
                return Long.MAX_VALUE - Long.parseLong(subString);
            }
        }
        return null;
    }

    /**
     * 获得hbase保存数据的单元格模型
     * @param rowKey : 行键值
     * @param columnName : 列名称
     * @param value : 列值
     * @return 单元格实体
     */
    public static CellModel getHbaseCellModel(String rowKey, String columnName, Object value) {
        CellModel cellModel = new CellModel();
        cellModel.setRowKey(rowKey);
        cellModel.setColumnFamily(HBaseConstant.FAMILY_COLUMN_NAME);
        cellModel.setColumn(columnName);
        cellModel.setValue(value);
        return cellModel;
    }

    /**
     * 获得时间间隔对应的天数集合
     * @return Map<Integer,Integer> key：表示时间间隔，value：表示当前时间间隔采样的阈值，超过这个天数则进行采样
     */
    public static Map<Integer,Integer> getInternalDayMap() {
        if (dayMap != null) {
            return dayMap;
        }
        dayMap = new HashMap<>(16);
        dayMap.put(10,2);
        dayMap.put(15,3);
        dayMap.put(20,4);
        dayMap.put(30,6);
        dayMap.put(60,12);
        return dayMap;
    }

    /**
     * 获取指定时间段内对应毫秒间隔的采样间隔
     * @return 采样时间间隔
     */
    public static long getSampleGap(Long beginTime, Long endTime, Integer gap) {
        long multiply = (long)Math.ceil(DateUtil.daysBetween(beginTime,endTime) / getInternalDayMap().get(gap));
        return BaseConstant.ONE_MINUTE_MILLISECOND * multiply * gap;
    }

    /**
     * 获取指定时间段内对应秒间隔的采样间隔
     * @return 采样时间间隔
     */
    public static Integer getSampleSecondGap(Long beginTime, Long endTime, Integer gap) {
        int multiply = (int)Math.ceil(DateUtil.daysBetween(beginTime,endTime) / getInternalDayMap().get(gap));
        return multiply * gap * BaseConstant.SIXTY_SECONDS;
    }
}
