package com.webank.wetoolscmdb.model.dto.cmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CmdbRequest {
    private String userAuthKey;
    private String type;
    private int startIndex;
    private int pageSize;
    private String action;
    private boolean isPaging;
    private Map<String, String> filter;
    private List<String> resultColumn;
}
