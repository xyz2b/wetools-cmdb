package com.webank.wetoolscmdb.model.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmdbRes {
    private Object data;
    private CmdbResponseHeaders headers;
    private String msg;
    private Integer retCode;
}