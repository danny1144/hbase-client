package com.siemens.hbase.hbaseclient.util.page;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author z00403vj
 */
@Data
public class TBData {
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPage;
    private List<Map<String, Object>> resultList;
    /**
     * 标题
     */
    private List<TitleColumns> columns;
}

