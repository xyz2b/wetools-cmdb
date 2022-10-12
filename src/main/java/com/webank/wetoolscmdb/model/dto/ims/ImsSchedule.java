package com.webank.wetoolscmdb.model.dto.ims;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsSchedule {
    private int scheduleId;
    private String scheduleName;
    private String subsystem;
    private List<ImsScheduleMetric> metricList;
}
