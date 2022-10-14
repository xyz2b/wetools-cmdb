package com.webank.wetoolscmdb.model.dto.ims;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImsComAlert {
    private String listId;
    private String alertId;
    private String recoverId;
    private int domainId;
    private String domainName;
    private int subSystemId;
    private int ciId;
    private int ciTypeId;
    private String alertTitle;
    private String alertObj;
    private String alertInfo;
    private String alertIp;
    private String alertDcn;
    private int alertLevel;
    private int alertOrigin;
    private int alertCount;
    private int alertState;
    private long firstAlertTime;
    private String firstAlertTimeStr;
    private long lastAlertTime;
    private String lastAlertTimeStr;
    private int alertVisibility;
    private int metricId;
    private long terminateTime;
    private long alertDuration;
    private String terminateTimeStr;
    private String terminateUser;
    private String terminateCode;
    private String terminateMessage;
    private int terminateItsm;
    private int handleState;
    private String handleUser;
    private long handleTime;
    private String handleTimeStr;
    private String remarkInfo;
    private long blockTime;
    private String blockTimeStr;
    private String blockUser;
    private String blockReason;
    private int blockPolicyId;
    private int terminateChangeId;
    private String terminateContactPerson;
    private int itsmTaskId;
    private int counts;
    private String alarmIp;
    private String visitIp;
    private int strategyId;
    private int useUmgPolicy;
    private String itsmUrl;
    private String otherInfo;
    private String rawAlertTime;
    private String metricList;
    private String attributeList;
    private int monitorId;
    private int objectId;
    private int isMisjudgement;
    private String misjudgeReason;
    private String phoneMsgStatus;
    private String phoneMsgStatusList;
    private List<String> alertTags;
    private String tagList;
    private int jobType;
    private String systemName;
    private String ciName;
    private String callState;
}
