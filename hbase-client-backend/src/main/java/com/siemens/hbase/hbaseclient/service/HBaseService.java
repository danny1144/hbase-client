package com.siemens.hbase.hbaseclient.service;

import com.siemens.hbase.hbaseclient.controller.req.HbasePageQuery;
import com.siemens.hbase.hbaseclient.model.*;
import com.siemens.hbase.hbaseclient.util.page.TBData;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.Filter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @date 2019/04/03
 * @description Hbase业务处理层
 */
public interface HBaseService {

    /**
     * 创建和base表
     *
     * @param tableDescriptor 表描述
     * @return void
     */
    void createTable(HTableDescriptor tableDescriptor) throws IOException;

    /**
     * 创建和base表
     *
     * @param tableName      表名
     * @param columnFamilies 列簇名称集合
     * @return void
     */
    void createTable(String tableName, List<String> columnFamilies);

    /**
     * 批量保存单元格数据记录
     *
     * @param tableName  表名
     * @param cellModels 单元格实体
     * @return void
     */
    void batchPut(String tableName, List<CellModel> cellModels);

    /**
     * 批量删除指行键集合的数据记录
     *
     * @param tableName 表名
     * @param rowKeys   行键集合
     * @return void
     */
    void batchDelete(String tableName, List<String> rowKeys);

    /**
     * 获取指定行指定簇对应列最新单元格数据对应的rowKey
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀值
     * @param familyName   列簇名称
     * @param qualifier    列名称
     * @return String 最新行的rowKey
     */
    String getCellLatestRowKey(String tableName, String rowKeyPrefix, String familyName, String qualifier);

    /**
     * 获取指定行指定簇对应列是否有数据，有数据则返回当前rowKey
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return String 当前rowKey
     */
    String getCellRowKey(String tableName, String rowKey, String familyName, String qualifier);

    /**
     * 获取指定行指定簇对应列Double单元格数据
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return Double 指定单元格内容
     */
    Double getCellDoubleValue(String tableName, String rowKey, String familyName, String qualifier);

    /**
     * 获取指定行指定簇对应列Long单元格数据
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return String 指定单元格内容
     */
    Long getCellLongValue(String tableName, String rowKey
            , String familyName, String qualifier);

    /**
     * 获取指定行指定簇对应列Integer单元格数据
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return String 指定单元格内容
     */
    Integer getCellIntegerValue(String tableName, String rowKey
            , String familyName, String qualifier);

    /**
     * 获取指定行所有簇对应列集合下的Double单元格数据Map
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @return Map<String, Double> 指定列对应的数值Map
     */
    Map<String, Double> getAllCellDoubleMap(String tableName, String rowKey
            , String familyName);

    /**
     * 获取指定行指定簇对应列集合下的Double单元格数据Map
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifiers 列名称集合
     * @return Map<String, Double> 指定列对应的数值Map
     */
    Map<String, Double> getCellDoubleMap(String tableName, String rowKey
            , String familyName, List<String> qualifiers);

    /**
     * 获取指定行指定簇对应列集合下的Double单元格数据Map
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifiers 列名称集合
     * @return Map<Long, Double> 指定列对应的数值Map
     */
    Map<Long, Double> getCellDoubleLongMap(String tableName, String rowKey
            , String familyName, List<Long> qualifiers);

    /**
     * 通过Double类型的指定参数得到需要进行所有过滤条件的过滤器
     *
     * @param familyName   列簇名称
     * @param filterModels 过滤条件集合
     * @return Filter
     */
    Filter getFilterListByDouble(String familyName, List<DoubleFilterModel> filterModels);

    /**
     * 获取指定行区间内指定列的偏差趋势模型
     *
     * @param tableName   表名
     * @param startRowKey 开始行键值
     * @param stopRowKey  结束行键值
     * @param familyName  列簇名称
     * @param qualifiers  列名集合,第一个元素值为最后要比较的列
     * @param filter      过滤条件
     * @return TrendModel
     */
    TrendModel getTrendModelByRowRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers, Filter filter);

    /**
     * 获取指定行区间内多个列的值，包含每个值对应的时间
     *
     * @param tableName   表名
     * @param startRowKey 开始行键值
     * @param stopRowKey  结束行键值
     * @param familyName  列簇名称
     * @param qualifiers  列名集合
     * @return MultipleTrendModel
     */
    MultipleTrendModel getMultipleTrendModelByRowRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers);

    /**
     * 获取指定行区间内多个列的值,不包含每个值对应的时间
     *
     * @param tableName   表名
     * @param startRowKey 开始行键值
     * @param stopRowKey  结束行键值
     * @param familyName  列簇名称
     * @param qualifiers  列名集合
     * @return MultipleValueModel
     */
    MultipleValueModel getMultipleValueModelByRowRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers);

    /**
     * 查询指定rowKey列表下的结果集
     *
     * @param tableName  表名
     * @param familyName 列簇名称
     * @param qualifiers 列集合
     * @param rowKeys    行键列表
     * @param filter     过滤器
     * @return TrendModel
     */
    TrendModel getTrendModelByRowKeys(String tableName
            , String familyName, List<String> qualifiers, List<String> rowKeys, Filter filter);

    /**
     * 查询指定rowKey下多个列结果集
     *
     * @param tableName  表名
     * @param familyName 列簇名称
     * @param qualifiers 列集合
     * @param rowKeys    行键列表
     * @return MultipleTrendModel
     */
    MultipleTrendModel getMultipleTrendModelByRowKeys(String tableName
            , String familyName, List<String> qualifiers, List<String> rowKeys);

    /**
     * 查询指定rowKey列表下的多个列结果集,不包含每个值对应的时间
     *
     * @param tableName  表名
     * @param familyName 列簇名称
     * @param qualifiers 列名集合
     * @param rowKeys    行键列表
     * @return MultipleValueModel
     */
    MultipleValueModel getMultipleValueModelByRowKeys(String tableName
            , String familyName, List<String> qualifiers, List<String> rowKeys);

    /**
     * 获取指定开始结束rowKey区间中最大的rowKey
     *
     * @param tableName   表名
     * @param startRowKey 开始行键值
     * @param stopRowKey  结束行键值
     * @param familyName  列簇名称
     * @param qualifiers  列名集合,第一个元素值为最后要比较的列
     * @param filter      过滤器
     * @return String 最大rowKey
     */
    String getLastRowKeyByRowKeyRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers, Filter filter);

    /**
     * 通过采样算法获取趋势模型
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @param familyName   列簇名称
     * @param qualifiers   列名集合,第一个元素值为最后要比较的列
     * @param filter       过滤器
     * @param timeGap      时间间隔
     * @return TrendModel 趋势模型
     */
    TrendModel getTrendBySampleAlgorithm(String tableName, String rowKeyPrefix
            , Long beginTime, Long endTime, String familyName, List<String> qualifiers, Filter filter, Integer timeGap);


    /**
     * 通过rowKey前缀获取最迟的rowKey
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀
     * @param familyName   列簇名称
     * @param qualifier    列名称
     * @return String 最迟时间rowKey
     */
    String getLastRowKeyByPrefix(String tableName, String rowKeyPrefix, String familyName, String qualifier);

    /**
     * 删除指定表对应rowKey前缀的所有数据
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀值 : rowKey是工程id
     * @param familyName   列簇名称
     * @param qualifier    列名称
     * @param timeGap      时间间隔
     * @return void
     */
    void deleteAllDataByRowKeyPrefix(String tableName, String rowKeyPrefix
            , String familyName, String qualifier, Integer timeGap);


    /**
     * 删除指定表对应rowKey前缀的所有数据
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀值 : rowKey是工程id
     * @param timeGap      时间间隔
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @param familyName   列簇名称
     * @param qualifier    列名称
     * @return void
     */
    void deleteAllDataByRowKeyPrefixAndTime(String tableName, String rowKeyPrefix, Integer timeGap
            , long beginTime, long endTime);

    /**
     * 通过采样算法获取多个指标趋势模型
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @param familyName   列簇名称
     * @param qualifiers   列名集合
     * @param timeGap      时间间隔
     * @return TrendModel 趋势模型
     */
    MultipleTrendModel getMultipleTrendBySampleAlgorithm(String tableName, String rowKeyPrefix
            , Long beginTime, Long endTime, String familyName, List<String> qualifiers, Integer timeGap);

    /**
     * @return 获得所有表名
     */
    List<String> getAllTableNames();

    /**
     * @return 命名空间
     */
    List<String> getAllNameSpace();

    ResultScanner queryData(String tableName, String startRowKey, int pageSize);

    /**
     * 分页查询
     *
     * @param tableName
     * @param rowKeyPrefix
     * @param startRow
     * @param stopRow
     * @param currentPage
     * @param pageSize
     * @return
     * @throws IOException
     */
    TBData getDataMap(HbasePageQuery query);
}
