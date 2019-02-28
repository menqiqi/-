package com.msq.mini_everything.core.model;

import lombok.Data;

@Data
public class Condition {

    private String name;

    private String fileType;

    private Integer limit;

    /**
     * 检索结果的文件信息的depth排序规则
     * 默认是true -> 升序
     * false -> desc
     */
    private Boolean orderByAsc;
}
