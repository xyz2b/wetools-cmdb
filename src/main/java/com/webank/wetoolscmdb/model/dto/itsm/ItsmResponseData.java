package com.webank.wetoolscmdb.model.dto.itsm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItsmResponseData {
    private List<Map<String, Object>> data;
    private int currentPage;
    private int firstResult;
    private int maxResult;
    private int pageSize;
    private int totalCount;
    private int totalPage;
}
