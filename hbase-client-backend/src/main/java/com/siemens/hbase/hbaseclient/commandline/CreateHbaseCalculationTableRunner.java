package com.siemens.hbase.hbaseclient.commandline;

import com.siemens.hbase.hbaseclient.service.HBaseService;
import com.siemens.hbase.hbaseclient.util.HBaseConstant;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author jin.liu@siemens.com
 * @date 2019/11/14
 * @description 在Hbase中创建性能监控计算数据表
 */
@Component
public class CreateHbaseCalculationTableRunner implements CommandLineRunner {

    @Autowired
    private HBaseService hBaseService;

    /**
     * 服务启动后，首先判断hbase计算数据表是否存在，如果不存在，则创建指定表
     * @return void
     */
    @Override
    public void run(String... strings) throws Exception{
        //性能计算表
        HTableDescriptor calculationTableDesc = new HTableDescriptor(TableName.valueOf(HBaseConstant.PERFORMANCE_TABLE_NAME));
        //偏差分析表
        HTableDescriptor deviationTableDesc = new HTableDescriptor(TableName.valueOf(HBaseConstant.DEVIATION_TABLE_NAME));
        //压气机水洗表
        HTableDescriptor compressorTableDesc = new HTableDescriptor(TableName.valueOf(HBaseConstant.COMPRESSOR_TABLE_NAME));
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(Bytes.toBytes(HBaseConstant.FAMILY_COLUMN_NAME));
        hColumnDescriptor.setCompactionCompressionType(Compression.Algorithm.SNAPPY);
        calculationTableDesc.addFamily(hColumnDescriptor);
        deviationTableDesc.addFamily(hColumnDescriptor);
        compressorTableDesc.addFamily(hColumnDescriptor);
        hBaseService.createTable(calculationTableDesc);
        hBaseService.createTable(deviationTableDesc);
        hBaseService.createTable(compressorTableDesc);
    }
}
