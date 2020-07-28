package com.siemens.hbase.hbaseclient.controller;

import com.siemens.hbase.hbaseclient.controller.req.HbasePageQuery;
import com.siemens.hbase.hbaseclient.controller.response.ResponseMessage;
import com.siemens.hbase.hbaseclient.model.CellModel;
import com.siemens.hbase.hbaseclient.service.HBaseService;
import com.siemens.hbase.hbaseclient.util.CompressorConstant;
import com.siemens.hbase.hbaseclient.util.DateUtil;
import com.siemens.hbase.hbaseclient.util.HBaseConstant;
import com.siemens.hbase.hbaseclient.util.HBaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @Description:
 * @author: zhongxp
 * @Date: 7/20/2020 4:54 PM
 */
@RestController
@RequestMapping("/v1")
public class RestClientController {

    @Autowired
    private HBaseService hBaseService;


    /**
     * @return 所有的表名称列表
     */
    @GetMapping(value = "/allTables")
    public ResponseMessage<Object> getTableNames() {
        List<String> list = hBaseService.getAllTableNames();
        return ResponseMessage.ok(list);
    }

    /**
     * @return 所有命名空间
     */
    @GetMapping(value = "/allNamespace")
    public ResponseMessage<Object> getallNamespace() {
        List<String> list = hBaseService.getAllNameSpace();
        return ResponseMessage.ok(list);
    }

    /**
     * @param tableName
     * @param rowkeys
     * @return 批量删除所有表
     */
    @DeleteMapping(value = "/all/{tableName}")
    public Object deleteTable(@PathVariable("tableName") String tableName, @RequestBody List<String> rowkeys) {
        hBaseService.batchDelete(tableName, rowkeys);
        return ResponseMessage.ok("删除成功");
    }

    /**
     * @param projectId
     * @return 批量插入压气机水洗表数据
     */
    @PostMapping(value = "/compressor/{projectId}")
    public Object getTableNames(@PathVariable("projectId") Long projectId) {
        Date now = DateUtil.getNowZeroSecondAndMillisecondDate();
        List<CellModel> cellModels = new ArrayList<>();
        Map<String, Double> resultMap = new HashMap<>(8);
        resultMap.put(CompressorConstant.COMPRESSOR_CONVERT_RATE, Math.random() * 100);
        resultMap.put(CompressorConstant.COMPRESSOR_PRESS_RATE, Math.random() * 100);
        resultMap.put(CompressorConstant.COMPRESSOR_CONVERT_FLOW, Math.random() * 100);
        resultMap.put(CompressorConstant.COMPRESSOR_CONVERT_EFF, Math.random() * 100);
        if (resultMap != null && !resultMap.isEmpty()) {
            String key = HBaseUtil.getRowKeyByPrefix(projectId.toString(), now.getTime());
            for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
                String itemKey = entry.getKey();
                cellModels.add(HBaseUtil.getHbaseCellModel(key, itemKey, entry.getValue()));
            }
            hBaseService.batchPut(HBaseConstant.COMPRESSOR_TABLE_NAME, cellModels);

        }
        return ResponseMessage.ok("批量插入成功");
    }


    /**
     * @return 分页查询
     */
    @PostMapping(value = "/pageQuery")
    public Object gePage(@RequestBody HbasePageQuery query)  {
        return ResponseMessage.ok(hBaseService.getDataMap(query));
    }

    /**
     * 批量插入member
     *
     * @return
     */
    @PostMapping(value = "/batchInsert")
    public Object batchInsertMember() {
        List<CellModel> cellModels = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            CellModel c = new CellModel();
            c.setRowKey("mb" + i);
            c.setColumnFamily("address");
            c.setColumn("city");
            c.setValue("陕西" + i);
            cellModels.add(c);

        }
        for (int i = 0; i < 100; i++) {
            CellModel c = new CellModel();
            c.setRowKey("mb" + i);
            c.setColumnFamily("address");
            c.setColumn("contry");
            c.setValue("中国" + i);
            cellModels.add(c);

        }
        for (int i = 0; i < 100; i++) {
            CellModel c = new CellModel();
            c.setRowKey("mb" + i);
            c.setColumnFamily("info");
            c.setColumn("id");
            c.setValue("" + i);
            cellModels.add(c);
        }
        for (int i = 0; i < 100; i++) {
            CellModel c = new CellModel();
            c.setRowKey("mb" + i);
            c.setColumnFamily("info");
            c.setColumn("age");
            c.setValue("" + i);
            cellModels.add(c);

        }
        hBaseService.batchPut("member", cellModels);
        return ResponseMessage.ok("批量插入成功");

    }
}