## hbase客户端

- 版本 hbase  1.2.0
  - 依赖zookeeper
## docker-hbase
hbase搭建一个单机版集群
datanode-1  数据节点，负责存储实际数据
hmaster-1 hbase主节点
namenode-1  它负责管理文件系统名称空间和bai控制外部客户机du的访问
regionserver-1 主要负责响应用户的请求，向 HDFS 读写数据。RegionServer 运行在 DataNode 服务器上，实现数据的本地性
zookeeper-1   主要用于实现高可靠性（High Availability, HA），包括 HDFS 的 NameNode 和 YARN 的 ResourceManager 的 HA

regionserver-1.vnet hmaster-1.vnet namenode-1.vnet datanode-1.vnet 这些dns需要在本地host进行映射
## spring-boot habase

## 解决HBase中snappy出错

**正常输出是这样的**

```
/opt/hbase/bin/hbase  org.apache.hadoop.hbase.util.CompressionTest  /root/anaconda-ks.cfg  snappy
2020-07-20 08:29:10,204 INFO  [main] Configuration.deprecation: hadoop.native.lib is deprecated. Instead, use io.native.lib.available
2020-07-20 08:29:12,100 INFO  [main] hfile.CacheConfig: CacheConfig:disabled
2020-07-20 08:29:12,489 INFO  [main] compress.CodecPool: Got brand-new compressor [.snappy]
2020-07-20 08:29:12,496 INFO  [main] compress.CodecPool: Got brand-new compressor [.snappy]
2020-07-20 08:29:13,391 INFO  [main] hfile.CacheConfig: CacheConfig:disabled
2020-07-20 08:29:13,417 INFO  [main] compress.CodecPool: Got brand-new decompressor [.snappy]
SUCCESS

```

**异常情况**
```
hbase org.apache.hadoop.hbase.util.CompressionTest ./README.txt  snappy
2020-07-20 08:32:58,814 WARN  [main] util.NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
2020-07-20 08:32:59,048 INFO  [main] hfile.CacheConfig: Created cacheConfig: CacheConfig:disabled
Exception in thread "main" java.lang.UnsatisfiedLinkError: org.apache.hadoop.util.NativeCodeLoader.buildSupportsSnappy()Z
        at org.apache.hadoop.util.NativeCodeLoader.buildSupportsSnappy(Native Method)
        at org.apache.hadoop.io.compress.SnappyCodec.checkNativeCodeLoaded(SnappyCodec.java:63)
        at org.apache.hadoop.io.compress.SnappyCodec.getCompressorType(SnappyCodec.java:132)
        at org.apache.hadoop.io.compress.CodecPool.getCompressor(CodecPool.java:148)
        at org.apache.hadoop.io.compress.CodecPool.getCompressor(CodecPool.java:163)
        at org.apache.hadoop.hbase.io.compress.Compression$Algorithm.getCompressor(Compression.java:330)
        at org.apache.hadoop.hbase.io.encoding.HFileBlockDefaultEncodingContext.<init>(HFileBlockDefaultEncodingContext.java:90)
        at org.apache.hadoop.hbase.io.hfile.HFileBlock$Writer.<init>(HFileBlock.java:879)
        at org.apache.hadoop.hbase.io.hfile.HFileWriterV2.finishInit(HFileWriterV2.java:126)
        at org.apache.hadoop.hbase.io.hfile.HFileWriterV2.<init>(HFileWriterV2.java:118)
        at org.apache.hadoop.hbase.io.hfile.HFileWriterV3.<init>(HFileWriterV3.java:67)
        at org.apache.hadoop.hbase.io.hfile.HFileWriterV3$WriterFactoryV3.createWriter(HFileWriterV3.java:59)
        at org.apache.hadoop.hbase.io.hfile.HFile$WriterFactory.create(HFile.java:309)
        at org.apache.hadoop.hbase.util.CompressionTest.doSmokeTest(CompressionTest.java:124)
        at org.apache.hadoop.hbase.util.CompressionTest.main(CompressionTest.java:160)
```









HBase Shell相关命令
hbase	shell命令	描述
create	创建表	< create ‘表名’, ‘列族名’, ‘列族名2’,‘列族名N’ >
list	查看所有表	< list all >
describe	显示表详细信息	< describe ‘表名’ >
exists	判断表是否存在	< exists ‘表名’ >
enable	使表有效	< enable ‘表名’ >
disable	使表无效	< disable ‘表名’ >
is_enabled	判断是否启动表	< is_enabled ‘表名’ >
is_disabled	判断是否禁用表	< is_disabled ‘表名’ >
count	统计表中行的数量	< count ‘表名’ >
put	添加记录	< put ‘表名’, ‘row key’, ‘列族1 : 列’, ‘值’ >
get	获取记录(row key下所有)	< get ‘表名’, ‘row key’>
get	获取记录(某个列族)	< get ‘表名’, ‘row key’, ‘列族’>
get	获取记录(某个列)	< get ‘表名’,‘row key’,‘列族:列’ >
delete	删除记录	< delete ‘表名’, ‘row key’, ‘列族:列’ >
deleteall	删除一行	< deleteall ‘表名’,‘row key’>
drop	删除表	<disable ‘表名’> < drop ‘表名’>
alter	修改列族（column family）	
incr	增加指定表，行或列的值	
truncate	清空表	逻辑为先删除后创建 <truncate ‘表明’>
scan	通过对表的扫描来获取对用的值	<scan ‘表名’>
tools	列出hbase所支持的工具	
status	返回hbase集群的状态信息	
version	返回hbase版本信息	
exit	退出hbase shell	
shutdown	关闭hbase集群(与exit不同)	
 


- 查看表有那些rowkey

  count "member",INTERVAL=>1


put 'member','mb1','info:id','3'
put 'member','mb1','info:age','11'
put 'member','mb1','address:city','shangh2ai'
put 'member','mb1','address:contry','china'
put 'member','mb2','info:id','4'
put 'member','mb2','info:age','32'
put 'member','mb2','address:city','beijin2g'
put 'member','mb2','address:contry','chi2na'

put 'member','mb3','info:id','5'
put 'member','mb3','info:age','11'
put 'member','mb3','address:city','shangh2ai'
put 'member','mb4','address:contry','china'
put 'member','mb2','info:id','6'
put 'member','mb2','info:age','32'
put 'member','mb2','address:city','beijin2g'
put 'member','mb2','address:contry','chi2na'  


["pma_compressor","pma_deviation","pma_optimization","pma_performance","segment_db0_kafka","value_db0_kafka"],

scan "pma_compressor"
scan "pma_deviation"
scan "pma_optimization"
scan "pma_performance"
scan "segment_db0_kafka"
scan "value_db0_kafka"

count "pma_compressor",INTERVAL=>1
```
hbase(main):005:0> get "pma_compressor","d3d9446802a44259755d38e6d163e820_9223370444531575807"
COLUMN                           CELL                                                                                       
 result:compressorConvertEff     timestamp=1593655620053, value=?\xEC\xB7\x0A\x17\xDB\xE9\xCB                               
 result:compressorConvertFlow    timestamp=1593655620053, value=?\xB4\x8F\xBB\x1Ey\x92\xBE                                  
 result:compressorConvertRate    timestamp=1593655620053, value=@eF\xB9\xF00\xFF\xFD                                        
 result:compressorPressRate      timestamp=1593655620053, value=@%yw\xD6\xFC%\xAB  
 
 ```
count "pma_deviation",INTERVAL=>1
```
hbase(main):007:0> get "pma_deviation","ff49740c0bfe60aaea7fa8c8fc362c36_9223370441542975807"
COLUMN                           CELL                                                                                       
 result:287                      timestamp=1595312472694, value=\x00\x00\x00\x00\x00\x00\x00\x00                            
 result:368                      timestamp=1595312472694, value=\x00\x00\x00\x00\x00\x00\x00\x00                            
 result:881                      timestamp=1595312472694, value=\x00\x00\x00\x00\x00\x00\x00\x00                            
 result:dataSet                  timestamp=1595312472694, value=@$\x00\x00\x00\x00\x00\x00 
 
 ```
count "pma_optimization",INTERVAL=>1

```
hbase(main):002:0> get "pma_optimization","fe771a932abc05affa9d11cb0e2e1d4d_9223370441646775807"

COLUMN                           CELL                                                                                       
 result:615:after                timestamp=1595208007555, value=A\x1A\x18\xBD\xEC\x99t\xFE                                  
 result:615:before               timestamp=1595208007555, value=A\x19\xF0\xA0\x00\x00\x00\x00                               
 result:616:after                timestamp=1595208007555, value=A\x1A5$Uh\xE1N                                              
 result:616:before               timestamp=1595208007555, value=A\x19\xF0\xA0\x00\x00\x00\x00                               
 result:618:after                timestamp=1595208007555, value=A\x04*\x80\x00\x00\x00\x00                                  
 result:618:before               timestamp=1595208007555, value=A\x04*\x80\x00\x00\x00\x00 
 ```
count "pma_performance",INTERVAL=>1
```
get "pma_performance","d3d9446802a44259755d38e6d163e820_9223370451184375807"

 result:32789                    timestamp=1595162393381, value=?\x93\xCE\xE9\xDD~\xCB\xB8                                  
 result:32790                    timestamp=1595162393397, value=@_\xD0\x00\x00\x00\x00\x00                                  
 result:32791                    timestamp=1595162393381, value=@e{\xD7\x0A=p\xA4                                           
 result:32792                    timestamp=1595162393381, value=@\x82\xD333333                                              
 result:32793                    timestamp=1595162393381, value=@\xAB\xE533333                                              
 result:32794                    timestamp=1595162393381, value=@\x1Am\xE0\x0D\x1Bqv                                        
 result:32795                    timestamp=1595162393381, value=?\x96\x18\x04\xD9\x83\x94u                                  
 result:32796                    timestamp=1595162393397, value=@_\xD0\x00\x00\x00\x00\x00                                  
 result:dataSet                  timestamp=1595162393397, value=@$\x00\x00\x00\x00\x00\x00                                  
 result:status                   timestamp=1595162393397, value=\x00\x00\x00\x00\x00\x00\x00\x00    
 ```