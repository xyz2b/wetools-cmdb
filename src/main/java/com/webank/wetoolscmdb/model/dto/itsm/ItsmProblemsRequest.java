package com.webank.wetoolscmdb.model.dto.itsm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItsmProblemsRequest {
    private int pageSize;
    private int currentPage;
    private long createDateSearchEnd;
    private long createDateSearchStart;
    private List<Integer> handlerTeamIds;
}
