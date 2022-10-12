package com.webank.wetoolscmdb.model.dto.ims;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsScheduleMetric {
    private int attrId;
    private String attrCode;
    private String attrName;
    private long collectTime;
    private long metricId;
    private double metricValue;
    private String objectName;
    private String valueUnit;
}
