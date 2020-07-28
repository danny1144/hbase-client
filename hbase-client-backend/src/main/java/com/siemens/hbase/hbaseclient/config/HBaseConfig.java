package com.siemens.hbase.hbaseclient.config;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jin.liu@siemens.com
 * @date 2019/11/01
 * @description HBase配置
 */
@Configuration
public class HBaseConfig {

    @Value("${hbase.zookeeper.quorum}")
    private String quorum;

    @Value("${hbase.rootdir}")
    private String rootDir;

    @Bean
    public HbaseTemplate hbaseTemplate() {
        HbaseTemplate hbaseTemplate = new HbaseTemplate();
        hbaseTemplate.setConfiguration(configuration());
        hbaseTemplate.setAutoFlush(true);
        return hbaseTemplate;
    }
    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        Map<String, String> config = initialConfigs();
        for (Map.Entry<String, String> map : config.entrySet()) {
            configuration.set(map.getKey(), map.getValue());
        }
        return configuration;
    }

    /**
     * hbase配置属性
     *
     * @return 配置信息映射
     */
    public Map<String, String> initialConfigs() {
        Map<String, String> propsMap = new HashMap<>(10);
        propsMap.put("hbase.zookeeper.quorum", quorum);
        propsMap.put("hbase.rootdir", rootDir);
        return propsMap;
    }
}
