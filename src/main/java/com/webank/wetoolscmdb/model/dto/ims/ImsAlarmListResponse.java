package com.webank.wetoolscmdb.model.dto.ims;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsAlarmListResponse {
    private int errorCode;
    private String errorMessage;
    private int totalCount;
    private int totalPageNum;
    private int currPageNum;
    private List<ImsComAlert> comAlertLists;
}
