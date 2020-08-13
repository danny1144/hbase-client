package com.siemens.hbase.hbaseclient.controller;

import com.siemens.hbase.hbaseclient.controller.req.HbasePageQuery;
import com.siemens.hbase.hbaseclient.controller.response.ResponseMessage;
import com.siemens.hbase.hbaseclient.service.HBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @return 分页查询
     */
    @PostMapping(value = "/pageQuery")
    public Object gePage(@RequestBody HbasePageQuery query) {
        return ResponseMessage.ok(hBaseService.getDataMap(query));
    }

}