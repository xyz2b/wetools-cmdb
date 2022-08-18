package com.webank.wetoolscmdb.model.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmdbResponse {
    private CmdbResponseData data;
    private CmdbResponseHeaders headers;
}