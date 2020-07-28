package com.siemens.hbase.hbaseclient.util.page;

import lombok.Data;

/**
 * 表头
 * @author z00403vj
 */
@Data
public class TitleColumns {
    private String title;
    private String dataIndex;
    private String key;
    private boolean ellipsis;
}