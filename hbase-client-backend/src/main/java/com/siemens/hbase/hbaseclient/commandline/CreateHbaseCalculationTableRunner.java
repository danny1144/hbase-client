package com.siemens.hbase.hbaseclient.commandline;

import com.siemens.hbase.hbaseclient.cache.SingletonMap;
import com.siemens.hbase.hbaseclient.service.HBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zxp@siemens.com
 * @date 2019/11/14
 * @description 在Hbase中创建性能监控计算数据表
 */
@Component
@Slf4j
public class CreateHbaseCalculationTableRunner implements CommandLineRunner {

    @Autowired
    private HBaseService hBaseService;
    @Autowired
    private HbaseTemplate hbaseTemplate;

    /**
     * 服务启动后，首先判断hbase计算数据表是否存在，如果不存在，则创建指定表
     *
     * @return void
     */
    @Override
    public void run(String... strings) throws IOException {
        List<String> allTableNames = hBaseService.getAllTableNames();
        Configuration configuration = hbaseTemplate.getConfiguration();
        Connection connection = null;
        try {
            connection = ConnectionFactory.createConnection(configuration);
            for (String tableName : allTableNames) {
                log.info("表名:{}", tableName);
                makeCacheTable(tableName, connection);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }
    private boolean makeCacheTable(String tableName, Connection connection) throws IOException {

        Table table = connection.getTable(TableName.valueOf(tableName));
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
        SingletonMap.getInstance().putValue(tableName, tableMetes);
        return false;
    }
}
