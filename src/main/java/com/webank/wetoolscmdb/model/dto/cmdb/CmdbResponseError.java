package com.webank.wetoolscmdb.model.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class CmdbResponseError {
    private Object data;
    private int retCode;
    private String msg;
}