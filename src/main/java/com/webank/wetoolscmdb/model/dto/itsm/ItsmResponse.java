package com.webank.wetoolscmdb.model.dto.itsm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItsmResponse {
    private ItsmResponseData data;
    private int retCode;
    private String retDetail;
}