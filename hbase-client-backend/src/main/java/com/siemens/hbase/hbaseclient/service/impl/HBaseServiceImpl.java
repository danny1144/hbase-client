package com.siemens.hbase.hbaseclient.service.impl;


import com.siemens.hbase.hbaseclient.cache.SingletonMap;
import com.siemens.hbase.hbaseclient.controller.req.HbasePageQuery;
import com.siemens.hbase.hbaseclient.model.*;
import com.siemens.hbase.hbaseclient.service.HBaseService;
import com.siemens.hbase.hbaseclient.util.BaseConstant;
import com.siemens.hbase.hbaseclient.util.DateUtil;
import com.siemens.hbase.hbaseclient.util.HBaseUtil;
import com.siemens.hbase.hbaseclient.util.page.TBData;
import com.siemens.hbase.hbaseclient.util.page.TitleColumns;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.ResultsExtractor;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zxp@siemens.com
 * @date 2019/04/03
 * @description HBase业务处理层
 */
@Service
public class HBaseServiceImpl implements HBaseService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HbaseTemplate hbaseTemplate;

    /**
     * 创建和base表
     *
     * @param tableDescriptor 表描述
     * @return void
     */
    @Override
    public void createTable(HTableDescriptor tableDescriptor) throws IOException {
        Configuration configuration = hbaseTemplate.getConfiguration();
        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(configuration);
            Admin admin = connection.getAdmin();
            if (admin.tableExists(tableDescriptor.getTableName())) {
                logger.info("The table exists!");
            } else {
                admin.createTable(tableDescriptor);
                logger.info("Create table successfully!");

            }
            makeCacheTable(tableDescriptor, connection);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    private boolean makeCacheTable(HTableDescriptor tableDescriptor, Connection connection) throws IOException {


        Table table = connection.getTable(tableDescriptor.getTableName());
        ResultScanner scanner = table.getScanner(new Scan());
        Result result = scanner.next();
        if (result == null) {
            return true;
        }
        List<Map<String, String>> tableMetes = new ArrayList<>(10);
        //result.advance()是否有下一个cell
        //result.current()获取当前cell
        while (result.advance()) {
            Map<String, String> tableMete = new HashMap<>(16);
            Cell currentCell = result.current();
            String row = Bytes.toString(CellUtil.cloneRow(currentCell));
            String family = Bytes.toString(CellUtil.cloneFamily(currentCell));
            String qualifier = Bytes.toString(CellUtil.cloneQualifier(currentCell));
            Bytes.toString(currentCell.getValueArray());
            Double value = Bytes.toDouble(CellUtil.cloneValue(currentCell));
            tableMete.put(family, qualifier);
            tableMetes.add(tableMete);
        }
        String tableName = tableDescriptor.getTableName().getNameAsString();
        SingletonMap.getInstance().putValue(tableName, tableMetes);
        return false;
    }

    /**
     * 创建和base表
     *
     * @param tableName      表名
     * @param columnFamilies 列簇名称集合
     * @return void
     */
    @Override
    public void createTable(String tableName, List<String> columnFamilies) {
        Connection connection = null;
        try {
            Configuration configuration = hbaseTemplate.getConfiguration();
            connection = ConnectionFactory.createConnection(configuration);
            Admin admin = connection.getAdmin();
            if (admin.tableExists(TableName.valueOf(tableName))) {
                logger.info("The table exists!");
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tableName));
                for (String columnFamily : columnFamilies) {
                    HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(Bytes.toBytes(columnFamily));
                    hColumnDescriptor.setCompactionCompressionType(Compression.Algorithm.SNAPPY);
                    tableDesc.addFamily(hColumnDescriptor);
                }
                admin.createTable(tableDesc);
                logger.info("Create table successfully!");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * 批量保存单元格数据记录
     *
     * @param tableName  表名
     * @param cellModels 单元格实体
     * @return void
     */
    @Override
    public void batchPut(String tableName, List<CellModel> cellModels) {
        if (cellModels == null || cellModels.size() == 0) {
            return;
        }
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            @Override
            public Object doInTable(HTableInterface htable) throws Throwable {
                List<Put> puts = new ArrayList<>(BaseConstant.BATCH_THRESHOLD);
                for (CellModel cellModel : cellModels) {
                    Put put = new Put(Bytes.toBytes(cellModel.getRowKey()));
                    if (cellModel.getTimestamp() == null) {
                        put.addColumn(Bytes.toBytes(cellModel.getColumnFamily()),
                                Bytes.toBytes(cellModel.getColumn()), HBaseUtil.getBytes(cellModel.getValue()));
                    } else {
                        put.addColumn(Bytes.toBytes(cellModel.getColumnFamily()),
                                Bytes.toBytes(cellModel.getColumn()), cellModel.getTimestamp()
                                , HBaseUtil.getBytes(cellModel.getValue()));
                    }
                    puts.add(put);
                    if (puts.size() >= BaseConstant.BATCH_THRESHOLD) {
                        htable.put(puts);
                        puts.clear();
                    }
                }
                if (puts.size() > 0) {
                    htable.put(puts);
                    puts.clear();
                }
                return null;
            }
        });
    }

    /**
     * 批量删除指行键集合的数据记录
     *
     * @param tableName 表名
     * @param rowKeys   行键集合
     * @return void
     */
    @Override
    public void batchDelete(String tableName, List<String> rowKeys) {
        if (rowKeys == null || rowKeys.size() == 0) {
            return;
        }
        hbaseTemplate.execute(tableName, new TableCallback<Object>() {
            @Override
            public Object doInTable(HTableInterface htable) throws Throwable {
                List<Delete> rows = new ArrayList<>(BaseConstant.BATCH_THRESHOLD);
                for (String rowKey : rowKeys) {
                    Delete row = new Delete(Bytes.toBytes(rowKey));
                    rows.add(row);
                    if (rows.size() >= BaseConstant.BATCH_THRESHOLD) {
                        htable.delete(rows);
                        rows.clear();
                    }
                }
                if (rows.size() > 0) {
                    htable.delete(rows);
                    rows.clear();
                }
                return null;
            }
        });
    }

    /**
     * 获取指定行指定簇对应列最新单元格数据对应的rowKey
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀值
     * @param familyName   列簇名称
     * @param qualifier    列名称
     * @return String 最新行的rowKey
     */
    @Override
    public String getCellLatestRowKey(String tableName, String rowKeyPrefix, String familyName, String qualifier) {
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
        scan.setRowPrefixFilter(Bytes.toBytes(rowKeyPrefix));
        scan.setCaching(BaseConstant.START);
        return hbaseTemplate.find(tableName, scan, new ResultsExtractor<String>() {
            @Override
            public String extractData(ResultScanner resultScanner) throws Exception {
                Result result = resultScanner.next();
                if (result == null) {
                    return null;
                }
                return Bytes.toString(result.getRow());
            }
        });
    }

    /**
     * 获取指定行指定簇对应列是否有数据，有数据则返回当前rowKey
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return String 当前rowKey
     */
    @Override
    public String getCellRowKey(String tableName, String rowKey
            , String familyName, String qualifier) {
        return hbaseTemplate.get(tableName, rowKey, familyName, qualifier, new RowMapper<String>() {
            @Override
            public String mapRow(Result result, int i) {
                if (result == null || result.isEmpty()) {
                    return null;
                }
                return rowKey;
            }
        });
    }

    /**
     * 获取指定行所有簇对应列集合下的Double单元格数据Map
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @return Map<String, Double> 指定列对应的数值Map
     */
    @Override
    public Map<String, Double> getAllCellDoubleMap(String tableName, String rowKey
            , String familyName) {
        return hbaseTemplate.execute(tableName, new TableCallback<Map<String, Double>>() {
            @Override
            public Map<String, Double> doInTable(HTableInterface htable) throws Throwable {
                Get get = new Get(Bytes.toBytes(rowKey));
                byte[] familyNameBytes = Bytes.toBytes(familyName);
                Result result = htable.get(get);
                if (result == null || result.isEmpty()) {
                    return null;
                }
                Map<byte[], byte[]> familyMap = result.getFamilyMap(Bytes.toBytes(familyName));
                Map<String, Double> map = new HashMap<>(30);
                for (Map.Entry<byte[], byte[]> entry : familyMap.entrySet()) {


                    map.put(Bytes.toString(entry.getKey()), Bytes.toDouble(result.getValue(familyNameBytes, entry.getKey())));
                }
                return map;
            }
        });
    }

    /**
     * 获取指定行指定簇对应列集合下的Double单元格数据Map
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifiers 列名称集合
     * @return Map<String, Double> 指定列对应的数值Map
     */
    @Override
    public Map<String, Double> getCellDoubleMap(String tableName, String rowKey
            , String familyName, List<String> qualifiers) {
        return hbaseTemplate.execute(tableName, new TableCallback<Map<String, Double>>() {
            @Override
            public Map<String, Double> doInTable(HTableInterface htable) throws Throwable {
                Get get = new Get(Bytes.toBytes(rowKey));
                byte[] familyNameBytes = Bytes.toBytes(familyName);
                for (String qualifier : qualifiers) {
                    get.addColumn(familyNameBytes, Bytes.toBytes(qualifier));
                }
                Result result = htable.get(get);
                if (result == null || result.isEmpty()) {
                    return null;
                }
                Map<String, Double> map = new HashMap<>(qualifiers.size() * 2);
                for (String qualifier : qualifiers) {
                    byte[] bytes = result.getValue(familyNameBytes, Bytes.toBytes(qualifier));
                    if (null != bytes && bytes.length > 0) {
                        map.put(qualifier, Bytes.toDouble(bytes));
                    }
                }
                return map;
            }
        });
    }

    /**
     * 获取指定行指定簇对应列集合下的Double单元格数据Map
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifiers 列名称集合
     * @return Map<Long, Double> 指定列对应的数值Map
     */
    @Override
    public Map<Long, Double> getCellDoubleLongMap(String tableName, String rowKey, String familyName, List<Long> qualifiers) {
        return hbaseTemplate.execute(tableName, new TableCallback<Map<Long, Double>>() {
            @Override
            public Map<Long, Double> doInTable(HTableInterface htable) throws Throwable {
                Get get = new Get(Bytes.toBytes(rowKey));
                byte[] familyNameBytes = Bytes.toBytes(familyName);
                for (Long qualifier : qualifiers) {
                    get.addColumn(familyNameBytes, Bytes.toBytes(qualifier.toString()));
                }
                Result result = htable.get(get);
                if (result == null || result.isEmpty()) {
                    return null;
                }
                Map<Long, Double> map = new HashMap<>(qualifiers.size() * 2);
                for (Long qualifier : qualifiers) {
                    map.put(qualifier, Bytes.toDouble(result.getValue(familyNameBytes, Bytes.toBytes(qualifier.toString()))));
                }
                return map;
            }
        });
    }

    /**
     * 获取指定行指定簇对应列Double单元格数据
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return String 指定单元格内容
     */
    @Override
    public Double getCellDoubleValue(String tableName, String rowKey
            , String familyName, String qualifier) {
        return hbaseTemplate.get(tableName, rowKey, familyName, qualifier, new RowMapper<Double>() {
            @Override
            public Double mapRow(Result result, int i) {
                if (result == null || result.isEmpty()) {
                    return null;
                }
                return Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                        , Bytes.toBytes(qualifier)));
            }
        });
    }

    /**
     * 获取指定行指定簇对应列Long单元格数据
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return String 指定单元格内容
     */
    @Override
    public Long getCellLongValue(String tableName, String rowKey
            , String familyName, String qualifier) {
        return hbaseTemplate.get(tableName, rowKey, familyName, qualifier, new RowMapper<Long>() {
            @Override
            public Long mapRow(Result result, int i) {
                if (result == null || result.isEmpty()) {
                    return null;
                }
                return Bytes.toLong(result.getValue(Bytes.toBytes(familyName)
                        , Bytes.toBytes(qualifier)));
            }
        });
    }

    /**
     * 获取指定行指定簇对应列Integer单元格数据
     *
     * @param tableName  表名
     * @param rowKey     行键值
     * @param familyName 列簇名称
     * @param qualifier  列名称
     * @return String 指定单元格内容
     */
    @Override
    public Integer getCellIntegerValue(String tableName, String rowKey
            , String familyName, String qualifier) {
        return hbaseTemplate.get(tableName, rowKey, familyName, qualifier, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(Result result, int i) {
                if (result == null || result.isEmpty()) {
                    return null;
                }
                return Bytes.toInt(result.getValue(Bytes.toBytes(familyName)
                        , Bytes.toBytes(qualifier)));
            }
        });
    }

    /**
     * 通过Double类型的指定参数得到需要进行所有过滤条件的过滤器
     *
     * @param familyName   列簇名称
     * @param filterModels 过滤条件集合
     * @return Filter
     */
    @Override
    public Filter getFilterListByDouble(String familyName, List<DoubleFilterModel> filterModels) {
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        if (filterModels != null && filterModels.size() > 0) {
            for (DoubleFilterModel filterModel : filterModels) {
                if (filterModel.getTop() != null) {
                    filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes(familyName)
                            , Bytes.toBytes(filterModel.getName())
                            , CompareFilter.CompareOp.LESS_OR_EQUAL
                            , Bytes.toBytes(filterModel.getBottom())));
                }
                if (filterModel.getBottom() != null) {
                    filterList.addFilter(new SingleColumnValueFilter(Bytes.toBytes(familyName)
                            , Bytes.toBytes(filterModel.getName())
                            , CompareFilter.CompareOp.GREATER_OR_EQUAL
                            , Bytes.toBytes(filterModel.getBottom())));
                }
            }
        }
        return filterList;
    }

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
    @Override
    public TrendModel getTrendModelByRowRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers, Filter filter) {
        Scan scan = new Scan();
        for (String qualifier : qualifiers) {
            //需要过滤的字段值，为了缩小最后返回result列的个数
            scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
        }
        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(stopRowKey));
        scan.setReversed(Boolean.TRUE);
        if (filter != null) {
            scan.setFilter(filter);
        }
        return hbaseTemplate.find(tableName, scan, new ResultsExtractor<TrendModel>() {
            @Override
            public TrendModel extractData(ResultScanner resultScanner) throws Exception {
                Result first = resultScanner.next();
                if (first == null) {
                    return null;
                }
                String qualifier = qualifiers.get(0);
                TrendModel trendModel = new TrendModel();
                List<Long> times = new ArrayList<>(10);
                List<Double> values = new ArrayList<>(10);
                trendModel.setTimes(times);
                trendModel.setValues(values);
                times.add(HBaseUtil.getTimestampInRowKey(Bytes.toString(first.getRow())));
                values.add(Bytes.toDouble(first.getValue(Bytes.toBytes(familyName)
                        , Bytes.toBytes(qualifier))));
                for (Result result : resultScanner) {
                    times.add(HBaseUtil.getTimestampInRowKey(Bytes.toString(result.getRow())));
                    values.add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                            , Bytes.toBytes(qualifier))));
                }
                return trendModel;
            }
        });
    }

    /**
     * 获取指定行区间内多个列的值,包含每个值对应的时间
     *
     * @param tableName   表名
     * @param startRowKey 开始行键值
     * @param stopRowKey  结束行键值
     * @param familyName  列簇名称
     * @param qualifiers  列名集合
     * @return MultipleTrendModel
     */
    @Override
    public MultipleTrendModel getMultipleTrendModelByRowRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers) {
        Scan scan = new Scan();
        for (String qualifier : qualifiers) {
            //需要过滤的字段值，为了缩小最后返回result列的个数
            scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
        }
        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(stopRowKey));
        scan.setReversed(Boolean.TRUE);
        return hbaseTemplate.find(tableName, scan, new ResultsExtractor<MultipleTrendModel>() {
            @Override
            public MultipleTrendModel extractData(ResultScanner resultScanner) throws Exception {
                Result first = resultScanner.next();
                if (first == null) {
                    return null;
                }
                MultipleTrendModel trendModel = new MultipleTrendModel();
                List<Long> times = new ArrayList<>(10);
                Map<String, List<Double>> maps = new HashMap<>(10);
                trendModel.setTimes(times);
                trendModel.setMaps(maps);
                times.add(HBaseUtil.getTimestampInRowKey(Bytes.toString(first.getRow())));
                for (String qualifier : qualifiers) {
                    if (!maps.containsKey(qualifier)) {
                        List<Double> values = new ArrayList<>(10);
                        values.add(Bytes.toDouble(first.getValue(Bytes.toBytes(familyName)
                                , Bytes.toBytes(qualifier))));
                        maps.put(qualifier, values);
                    } else {
                        List<Double> values = maps.get(qualifier);
                        values.add(Bytes.toDouble(first.getValue(Bytes.toBytes(familyName)
                                , Bytes.toBytes(qualifier))));
                    }
                }
                for (Result result : resultScanner) {
                    times.add(HBaseUtil.getTimestampInRowKey(Bytes.toString(result.getRow())));
                    for (String qualifier : qualifiers) {
                        maps.get(qualifier).add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                                , Bytes.toBytes(qualifier))));
                    }
                }
                return trendModel;
            }
        });
    }

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
    @Override
    public MultipleValueModel getMultipleValueModelByRowRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers) {
        Scan scan = new Scan();
        for (String qualifier : qualifiers) {
            //需要过滤的字段值，为了缩小最后返回result列的个数
            scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
        }
        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(stopRowKey));
        scan.setReversed(Boolean.TRUE);
        return hbaseTemplate.find(tableName, scan, new ResultsExtractor<MultipleValueModel>() {
            @Override
            public MultipleValueModel extractData(ResultScanner resultScanner) throws Exception {
                Result first = resultScanner.next();
                if (first == null) {
                    return null;
                }
                MultipleValueModel valueModel = new MultipleValueModel();
                Map<String, List<Double>> maps = new HashMap<>(10);
                valueModel.setMaps(maps);
                for (String qualifier : qualifiers) {
                    if (!maps.containsKey(qualifier)) {
                        List<Double> values = new ArrayList<>(10);
                        values.add(Bytes.toDouble(first.getValue(Bytes.toBytes(familyName)
                                , Bytes.toBytes(qualifier))));
                        maps.put(qualifier, values);
                    } else {
                        List<Double> values = maps.get(qualifier);
                        values.add(Bytes.toDouble(first.getValue(Bytes.toBytes(familyName)
                                , Bytes.toBytes(qualifier))));
                    }
                }
                for (Result result : resultScanner) {
                    for (String qualifier : qualifiers) {
                        maps.get(qualifier).add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                                , Bytes.toBytes(qualifier))));
                    }
                }
                return valueModel;
            }
        });
    }

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
    @Override
    public TrendModel getTrendModelByRowKeys(String tableName
            , String familyName, List<String> qualifiers, List<String> rowKeys, Filter filter) {
        return hbaseTemplate.execute(tableName, new TableCallback<TrendModel>() {
            @Override
            public TrendModel doInTable(HTableInterface htable) throws Throwable {
                List<Get> getList = new ArrayList(rowKeys.size());
                for (String rowKey : rowKeys) {
                    Get get = new Get(Bytes.toBytes(rowKey));
                    for (String qualifier : qualifiers) {
                        //需要过滤的字段值，为了缩小最后返回result列的个数
                        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
                    }
                    if (filter != null) {
                        get.setFilter(filter);
                    }
                    getList.add(get);
                }
                Result[] results = htable.get(getList);
                if (results != null && results.length > 0) {
                    List<Long> times = new ArrayList<>(200);
                    List<Double> values = new ArrayList<>(200);
                    for (Result result : results) {
                        if (result != null && !result.isEmpty()) {
                            times.add(HBaseUtil.getTimestampInRowKey(Bytes.toString(result.getRow())));
                            values.add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                                    , Bytes.toBytes(qualifiers.get(0)))));
                        }
                    }
                    if (times.isEmpty()) {
                        return null;
                    }
                    TrendModel trendModel = new TrendModel();
                    trendModel.setTimes(times);
                    trendModel.setValues(values);
                    return trendModel;
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * 查询指定rowKey下多个列结果集
     *
     * @param tableName  表名
     * @param familyName 列簇名称
     * @param qualifiers 列集合
     * @param rowKeys    行键列表
     * @return MultipleTrendModel
     */
    @Override
    public MultipleTrendModel getMultipleTrendModelByRowKeys(String tableName
            , String familyName, List<String> qualifiers, List<String> rowKeys) {
        return hbaseTemplate.execute(tableName, new TableCallback<MultipleTrendModel>() {
            @Override
            public MultipleTrendModel doInTable(HTableInterface htable) throws Throwable {
                List<Get> getList = new ArrayList(rowKeys.size());
                for (String rowKey : rowKeys) {
                    Get get = new Get(Bytes.toBytes(rowKey));
                    for (String qualifier : qualifiers) {
                        //需要过滤的字段值，为了缩小最后返回result列的个数
                        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
                    }
                    getList.add(get);
                }
                Result[] results = htable.get(getList);
                if (results != null && results.length > 0) {
                    List<Long> times = new ArrayList<>(200);
                    Map<String, List<Double>> maps = new HashMap<>(300);
                    for (Result result : results) {
                        if (result != null && !result.isEmpty()) {
                            times.add(HBaseUtil.getTimestampInRowKey(Bytes.toString(result.getRow())));
                            for (String qualifier : qualifiers) {
                                if (!maps.containsKey(qualifier)) {
                                    List<Double> values = new ArrayList<>(10);
                                    values.add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                                            , Bytes.toBytes(qualifier))));
                                    maps.put(qualifier, values);
                                } else {
                                    List<Double> values = maps.get(qualifier);
                                    values.add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                                            , Bytes.toBytes(qualifier))));
                                }
                            }

                        }
                    }
                    if (times.isEmpty()) {
                        return null;
                    }
                    MultipleTrendModel trendModel = new MultipleTrendModel();
                    trendModel.setTimes(times);
                    trendModel.setMaps(maps);
                    return trendModel;
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * 查询指定rowKey列表下的多个列结果集,不包含每个值对应的时间
     *
     * @param tableName  表名
     * @param familyName 列簇名称
     * @param qualifiers 列名集合
     * @param rowKeys    行键列表
     * @return MultipleValueModel
     */
    @Override
    public MultipleValueModel getMultipleValueModelByRowKeys(String tableName
            , String familyName, List<String> qualifiers, List<String> rowKeys) {
        return hbaseTemplate.execute(tableName, new TableCallback<MultipleValueModel>() {
            @Override
            public MultipleValueModel doInTable(HTableInterface htable) throws Throwable {
                List<Get> getList = new ArrayList(rowKeys.size());
                for (String rowKey : rowKeys) {
                    Get get = new Get(Bytes.toBytes(rowKey));
                    for (String qualifier : qualifiers) {
                        //需要过滤的字段值，为了缩小最后返回result列的个数
                        get.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
                    }
                    getList.add(get);
                }
                Result[] results = htable.get(getList);
                if (results != null && results.length > 0) {
                    Map<String, List<Double>> maps = new HashMap<>(300);
                    for (Result result : results) {
                        if (result != null && !result.isEmpty()) {
                            for (String qualifier : qualifiers) {
                                if (!maps.containsKey(qualifier)) {
                                    List<Double> values = new ArrayList<>(10);
                                    values.add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                                            , Bytes.toBytes(qualifier))));
                                    maps.put(qualifier, values);
                                } else {
                                    List<Double> values = maps.get(qualifier);
                                    values.add(Bytes.toDouble(result.getValue(Bytes.toBytes(familyName)
                                            , Bytes.toBytes(qualifier))));
                                }
                            }

                        }
                    }
                    if (maps.isEmpty()) {
                        return null;
                    }
                    MultipleValueModel trendModel = new MultipleValueModel();
                    trendModel.setMaps(maps);
                    return trendModel;
                } else {
                    return null;
                }
            }
        });
    }

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
    @Override
    public String getLastRowKeyByRowKeyRange(String tableName
            , String startRowKey, String stopRowKey, String familyName, List<String> qualifiers, Filter filter) {
        Scan scan = new Scan();
        for (String qualifier : qualifiers) {
            //需要过滤的字段值，为了缩小最后返回result列的个数
            scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
        }
        scan.withStartRow(Bytes.toBytes(startRowKey));
        scan.withStopRow(Bytes.toBytes(stopRowKey));
        scan.setReversed(Boolean.TRUE);
        if (filter != null) {
            scan.setFilter(filter);
        }
        scan.setCaching(BaseConstant.START);
        return hbaseTemplate.find(tableName, scan, new ResultsExtractor<String>() {
            @Override
            public String extractData(ResultScanner resultScanner) throws Exception {
                Result result = resultScanner.next();
                if (result == null) {
                    return null;
                }
                return Bytes.toString(result.getRow());
            }
        });
    }

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
    @Override
    public TrendModel getTrendBySampleAlgorithm(String tableName, String rowKeyPrefix
            , Long beginTime, Long endTime, String familyName, List<String> qualifiers, Filter filter, Integer timeGap) {
        long sampleGap = HBaseUtil.getSampleGap(beginTime, endTime, timeGap);
        String startRowKey = HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, beginTime);
        //结束日期加一是为了包含终止时间
        String stopRowKey = HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, endTime + 1);
        if (sampleGap <= timeGap * BaseConstant.ONE_MINUTE_MILLISECOND) {
            return getTrendModelByRowRange(tableName, startRowKey, stopRowKey, familyName, qualifiers, filter);
        } else {
            String lastKey = getLastRowKeyByRowKeyRange(tableName, startRowKey, stopRowKey, familyName, qualifiers, filter);
            if (lastKey == null) {
                return null;
            }
            List<String> rowKeys = new ArrayList<>(200);
            long time = HBaseUtil.getTimestampInRowKey(lastKey);
            for (; time <= endTime; ) {
                rowKeys.add(HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, time));
                time = time + sampleGap;
            }
            return getTrendModelByRowKeys(tableName, familyName, qualifiers, rowKeys, filter);
        }
    }


    /**
     * 通过rowKey前缀获取最迟的rowKey
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀
     * @param familyName   列簇名称
     * @param qualifier    列名称
     * @return String 最迟时间rowKey(也就是hbase过去时间第一条数据对应的rowKey)
     */
    @Override
    public String getLastRowKeyByPrefix(String tableName, String rowKeyPrefix, String familyName, String qualifier) {
        Scan scan = new Scan();
        scan.addColumn(Bytes.toBytes(familyName), Bytes.toBytes(qualifier));
        scan.withStartRow(Bytes.toBytes(String.format(BaseConstant.UNDERLINE_CHARACTER
                , rowKeyPrefix, (Long.MAX_VALUE - BaseConstant.INITIAL_ZERO))));
        scan.withStopRow(Bytes.toBytes(String.format(BaseConstant.UNDERLINE_CHARACTER
                , rowKeyPrefix, (Long.MAX_VALUE - System.currentTimeMillis()))));
        scan.setReversed(Boolean.TRUE);
        scan.setCaching(BaseConstant.START);
        return hbaseTemplate.find(tableName, scan, new ResultsExtractor<String>() {
            @Override
            public String extractData(ResultScanner resultScanner) throws Exception {
                Result result = resultScanner.next();
                if (result == null) {
                    return null;
                }
                return Bytes.toString(result.getRow());
            }
        });
    }

    /**
     * 删除指定表对应rowKey前缀的所有数据
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀值 : rowKey是工程id:设备id的hash值
     * @param familyName   列簇名称
     * @param qualifier    列名称
     * @param timeGap      时间间隔
     * @return void
     */
    @Override
    public void deleteAllDataByRowKeyPrefix(String tableName, String rowKeyPrefix
            , String familyName, String qualifier, Integer timeGap) {
        String latestKey = getCellLatestRowKey(tableName, rowKeyPrefix, familyName, qualifier);
        if (latestKey != null) {
            String lastKey = getLastRowKeyByPrefix(tableName, rowKeyPrefix, familyName, qualifier);
            if (latestKey.equals(lastKey)) {
                hbaseTemplate.delete(tableName, latestKey, familyName);
            } else {
                long beginTime = HBaseUtil.getTimestampInRowKey(lastKey);
                long endTime = HBaseUtil.getTimestampInRowKey(latestKey);
                List<String> rowKeys = new ArrayList<>(BaseConstant.BATCH_THRESHOLD);
                long gapTime = timeGap * BaseConstant.ONE_MINUTE_MILLISECOND;
                for (; beginTime <= endTime; ) {
                    rowKeys.add(HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, beginTime));
                    if (rowKeys.size() >= BaseConstant.BATCH_THRESHOLD) {
                        batchDelete(tableName, rowKeys);
                        rowKeys.clear();
                    }
                    beginTime = beginTime + gapTime;
                }
                rowKeys.add(latestKey);
                if (rowKeys.size() > 0) {
                    batchDelete(tableName, rowKeys);
                    rowKeys.clear();
                }
            }
        }
    }

    /**
     * 删除指定表对应rowKey前缀的所有数据
     *
     * @param tableName    表名
     * @param rowKeyPrefix 行键前缀值 : rowKey是工程id
     * @param timeGap      时间间隔
     * @param beginTime    开始时间
     * @param endTime      结束时间
     * @return void
     */
    @Override
    public void deleteAllDataByRowKeyPrefixAndTime(String tableName, String rowKeyPrefix, Integer timeGap
            , long beginTime, long endTime) {
        int minute = DateUtil.getMinute(new Date(beginTime));
        int remainder = minute % timeGap;
        beginTime = remainder * BaseConstant.ONE_MINUTE_MILLISECOND + beginTime;
        beginTime = DateUtil.getNowZeroSecondAndMillisecondDate(beginTime);
        List<String> rowKeys = new ArrayList<>(BaseConstant.BATCH_THRESHOLD);
        long gapTime = timeGap * BaseConstant.ONE_MINUTE_MILLISECOND;
        for (; beginTime <= endTime; ) {
            rowKeys.add(HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, beginTime));
            if (rowKeys.size() >= BaseConstant.BATCH_THRESHOLD) {
                batchDelete(tableName, rowKeys);
                rowKeys.clear();
            }
            beginTime = beginTime + gapTime;
        }
        if (rowKeys.size() > 0) {
            batchDelete(tableName, rowKeys);
            rowKeys.clear();
        }
    }

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
    @Override
    public MultipleTrendModel getMultipleTrendBySampleAlgorithm(String tableName, String rowKeyPrefix
            , Long beginTime, Long endTime, String familyName, List<String> qualifiers, Integer timeGap) {
        long sampleGap = HBaseUtil.getSampleGap(beginTime, endTime, timeGap);
        String startRowKey = HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, beginTime);
        //结束日期加一是为了包含终止时间
        String stopRowKey = HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, endTime + 1);
        if (sampleGap <= timeGap * BaseConstant.ONE_MINUTE_MILLISECOND) {
            return getMultipleTrendModelByRowRange(tableName, startRowKey, stopRowKey, familyName, qualifiers);
        } else {
            String lastKey = getLastRowKeyByRowKeyRange(tableName, startRowKey, stopRowKey, familyName, qualifiers, null);
            if (lastKey == null) {
                return null;
            }
            List<String> rowKeys = new ArrayList<>(200);
            long time = HBaseUtil.getTimestampInRowKey(lastKey);
            for (; time <= endTime; ) {
                rowKeys.add(HBaseUtil.getRowKeyByHashPrefix(rowKeyPrefix, time));
                time = time + sampleGap;
            }
            return getMultipleTrendModelByRowKeys(tableName, familyName, qualifiers, rowKeys);
        }
    }

    /**
     * @return 获取所有表名
     */
    @Override
    public List<String> getAllTableNames() {
        {
            Connection connection = null;
            try {
                Configuration configuration = hbaseTemplate.getConfiguration();
                connection = ConnectionFactory.createConnection(configuration);
                Admin admin = connection.getAdmin();
                TableName[] tableNames = admin.listTableNames();
                List<String> tableNamestr = Arrays.asList(tableNames).stream().map(x -> x.getNameAsString()).collect(Collectors.toList());
                return tableNamestr;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (null != connection) {
                    try {
                        connection.close();
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
            }
        }
        return null;

    }

    /**
     * @return 获取所有命名空间
     */
    @Override
    public List<String> getAllNameSpace() {
        {
            Connection connection = null;
            try {
                Configuration configuration = hbaseTemplate.getConfiguration();
                connection = ConnectionFactory.createConnection(configuration);
                Admin admin = connection.getAdmin();
                NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
                List<String> namespaces = Arrays.asList(namespaceDescriptors).stream().map(x -> x.getName()).collect(Collectors.toList());
                return namespaces;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                if (null != connection) {
                    try {
                        connection.close();
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
            }
        }


        return null;

    }


    /**
     * 功能描述: 获取分页数据
     *
     * @param tableName   表名
     * @param currentPage 当前页码
     * @param pageSize    每页条数
     * @return java.lang.String
     * @author xiaoming
     * @date 2020-5-26
     */
    private ResultScanner queryDataByPage(String tableName, int currentPage, int pageSize) {
        // 第一次查询时startRowKey为null
        String startRowKey = null;
        ResultScanner results = null;
        // 从第一页开始查询每页的数据
        for (int i = 0; i < currentPage; i++) {
            // 根据每一次传入的rowkey, 查询出排列顺序小于该 rowkey 的 pageSize 条数据, 则最后一页(currentPage)的数据就是最后一次查询的结果
            results = queryData(tableName, startRowKey, pageSize);
            if (i < currentPage - 1) {
                Iterator<Result> iterator = results.iterator();
                while (iterator.hasNext()) {
                    // 将每一页的最后一条数据做为下一页查询的起始行(不包含该条数据)
                    startRowKey = Bytes.toString(iterator.next().getRow());
                }
            }
        }
        return results;
    }


    /**
     * 功能描述: 查询数据
     *
     * @param tableName   表名
     * @param startRowKey 每页起始rowkey
     * @param pageSize    每页条数
     * @return org.apache.hadoop.hbase.client.ResultScanner
     * @author xiaoming
     * @date 2020-5-26
     */
    @Override
    public ResultScanner queryData(String tableName, String startRowKey, int pageSize) {
        HTable table = null;
        ResultScanner results = null;

        Scan scan = new Scan();
        // 设置倒序扫描(倒序查询的关键)
        scan.setReversed(true);
        // MUST_PASS_ALL 表示需要满足过滤器集合中的所有的filter
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        // 设置查询条数
        PageFilter pageFilter = new PageFilter(pageSize);
        filterList.addFilter(pageFilter);

        // 如果查询到了 startRowKey, 则过滤比 startRowKey 大的值
        if (StringUtils.isNotBlank(startRowKey)) {
            RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.LESS, new BinaryComparator(Bytes.toBytes(startRowKey)));
            filterList.addFilter(rowFilter);
        }
        scan.setFilter(filterList);
        Connection connection = null;
        try {
            Configuration configuration = hbaseTemplate.getConfiguration();
            connection = ConnectionFactory.createConnection(configuration);
            table = (HTable) connection.getTable(TableName.valueOf(tableName));
            results = table.getScanner(scan);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
            if (null != connection) {
                try {
                    connection.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
        return results;
    }

    /*
     * 获取hbase的表
     */
    public HTableInterface getTable(String tableName) {

        if (StringUtils.isEmpty(tableName)) {
            return null;
        }
        Configuration configuration = hbaseTemplate.getConfiguration();
        HTablePool tp = new HTablePool(configuration, 10);
        return tp.getTable(getBytes(tableName));
    }

    /* 转换byte数组 */
    private byte[] getBytes(String str) {
        if (str == null) {
            str = "";
        }

        return Bytes.toBytes(str);
    }

    /**
     * 查询数据
     *
     * @return 结果集
     */
    @Override
    public TBData getDataMap(HbasePageQuery query) {
        List<Map<String, Object>> mapList = new LinkedList<>();
        Integer pageSize = query.getPageSize(), currentPage = query.getCurrentPage();
        ResultScanner scanner = null;
        // 为分页创建的封装类对象，下面有给出具体属性
        TBData tbData = null;
        try {
            // 获取最大返回结果数量
            if (pageSize == null || pageSize == 0L) {
                pageSize = 100;
            }
            if (currentPage == null || currentPage == 0) {
                currentPage = 1;
            }

            // 计算起始页和结束页
            Integer firstPage = (currentPage - 1) * pageSize;
            Integer endPage = firstPage + pageSize;
            // 从表池中取出HBASE表对象
            HTableInterface table = getTable(query.getTableName());
            // 获取筛选对象
            Scan scan = getScan(query.getStartRowKey(), query.getEndRowKey(), query.getRowKeyPrefix());
            // 给筛选对象放入过滤器(true标识分页,具体方法在下面)
            scan.setFilter(packageFilters(true));
            if (StringUtils.isNotBlank(query.getColumnFamily()) && StringUtils.isNotBlank(query.getColumn())) {
                scan.addColumn(Bytes.toBytes(query.getColumnFamily()), Bytes.toBytes(query.getColumn()));
            }
            // 缓存1000条数据
            scan.setCaching(1000);
            scan.setCacheBlocks(false);
            scanner = table.getScanner(scan);
            int i = 0;
            List<byte[]> rowList = new LinkedList<byte[]>();
            // 遍历扫描器对象， 并将需要查询出来的数据row key取出
            for (Result result : scanner) {
                String row = toStr(result.getRow());
                if (i >= firstPage && i < endPage) {
                    rowList.add(getBytes(row));
                }
                i++;
            }
            List<Map<String, String>> valueByKey = (List<Map<String, String>>) SingletonMap.getInstance().getValueByKey(query.getTableName());
            // 获取取出的row key的GET对象
            List<Get> getList = getList(rowList, valueByKey);
            Result[] results = table.get(getList);
            // 遍历结果
            for (Result result : results) {
                Map<byte[], byte[]> fmap = packFamilyMap(result, valueByKey);
                Map<String, Object> rmap = packRowMap(fmap);
                mapList.add(rmap);
            }

            // 封装分页对象
            tbData = new TBData();
            tbData.setCurrentPage(currentPage);
            tbData.setPageSize(pageSize);
            tbData.setTotalCount(i);
            tbData.setTotalPage(getTotalPage(pageSize, i));
            tbData.setResultList(mapList);
            tbData.setColumns(generateTitle(valueByKey));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("參數錯誤");
        } finally {
            closeScanner(scanner);
        }

        return tbData;
    }

    private int getTotalPage(int pageSize, int totalCount) {
        int n = totalCount / pageSize;
        if (totalCount % pageSize == 0) {
            return n;
        } else {
            return ((int) n) + 1;
        }
    }

    // 获取扫描器对象
    private Scan getScan(String startRow, String stopRow, String rowKeyPrefix) {
        Scan scan = new Scan();
        scan.setStartRow(getBytes(startRow));
        scan.setStopRow(getBytes(stopRow));
        scan.setRowPrefixFilter(getBytes(rowKeyPrefix));
        return scan;
    }

    /**
     * 封装查询条件
     */
    private FilterList packageFilters(boolean isPage) {
        FilterList filterList = null;
        // MUST_PASS_ALL(条件 AND) MUST_PASS_ONE（条件OR）
        filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        Filter filter1 = null;
        Filter filter2 = null;
        filter1 = newFilter(getBytes("family1"), getBytes("column1"),
                CompareFilter.CompareOp.EQUAL, getBytes("condition1"));
        filter2 = newFilter(getBytes("family2"), getBytes("column1"),
                CompareFilter.CompareOp.LESS, getBytes("condition2"));
        //filterList.addFilter(filter1);
        //filterList.addFilter(filter2);
        if (isPage) {
            filterList.addFilter(new FirstKeyOnlyFilter());
        }
        return filterList;
    }

    private Filter newFilter(byte[] f, byte[] c, CompareFilter.CompareOp op, byte[] v) {
        return new SingleColumnValueFilter(f, c, op, v);
    }

    private void closeScanner(ResultScanner scanner) {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * 封装每行数据
     */
    private Map<String, Object> packRowMap(Map<byte[], byte[]> dataMap) {
        Map<String, Object> map = new LinkedHashMap<>();

        for (byte[] key : dataMap.keySet()) {

            byte[] value = dataMap.get(key);
            map.put(toStr(key), Bytes.toDouble(value));
        }
        return map;
    }

    /* 根据ROW KEY集合获取GET对象集合 */
    private List<Get> getList(List<byte[]> rowList, List<Map<String, String>> famillyToQualifier) {
        List<Get> list = new LinkedList<Get>();
        for (byte[] row : rowList) {
            Get get = new Get(row);
            for (Map<String, String> map : famillyToQualifier) {
                for (String key : map.keySet()) {
                    get.addColumn(getBytes(key), getBytes(map.get(key)));
                }
            }
            list.add(get);
        }
        return list;
    }

    private static List<TitleColumns> generateTitle(List<Map<String, String>> famillyToQualifier) {

        List<TitleColumns> titles = new ArrayList<>();
        if (CollectionUtils.isEmpty(famillyToQualifier)) {
            return titles;
        }
        for (Map<String, String> map : famillyToQualifier) {
            map.values().forEach(x -> {
                TitleColumns title = new TitleColumns();
                title.setDataIndex(x);
                title.setTitle(x);
                title.setEllipsis(true);
                title.setKey(x);
                titles.add(title);
            });
        }
        return titles;

    }

    /**
     * 封装配置的所有字段列族
     */
    private Map<byte[], byte[]> packFamilyMap(Result result, List<Map<String, String>> keyValues) {
        Map<byte[], byte[]> dataMap = new LinkedHashMap<byte[], byte[]>();
        Set<String> famillys = new HashSet<>();
        for (Map<String, String> map : keyValues) {
            Set<String> keys = map.keySet();
            for (String familly : keys) {
                famillys.add(familly);
            }
        }
        for (String familly : famillys) {
            dataMap.putAll(result.getFamilyMap(getBytes(familly)));
        }
        return dataMap;
    }

    private static String toStr(byte[] bt) {
        return Bytes.toString(bt);
    }


}
