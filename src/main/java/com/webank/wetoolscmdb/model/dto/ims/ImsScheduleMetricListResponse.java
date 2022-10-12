package com.webank.wetoolscmdb.model.dto.ims;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsScheduleMetricListResponse {
    private int code;
    private String msg;
    private List<ImsScheduleMetricListResponseData> data;
    private String costTime;
}
