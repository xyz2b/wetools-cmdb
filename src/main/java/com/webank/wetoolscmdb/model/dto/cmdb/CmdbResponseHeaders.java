package com.webank.wetoolscmdb.model.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmdbResponseHeaders {
    private String msg;
    private String retDetail;
    private String permissionType;
    private String startIndex;
    private String pageSize;
    private String totalRows;
    private String errorInfo;
    private int retCode;
    private int contentRows;
}
