package com.webank.wetoolscmdb.model.dto.ims;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsScheduleMetricListResponseData {
    private String ip;
    private List<ImsSchedule> scheduleList;
}
