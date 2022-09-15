package com.webank.wetoolscmdb.model.dto.itsm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItsmProblemsResponse {
    private long id;
    private String problemTitle;
    private String problemStatus;
    private String sourceName;
    private String priorityLevel;
    private String createDate;
    private String planDate;
    private String solveDate;
    private String solveUser;
    private String solveUserName;
    private String solveTeamName;
    private int solveTeamId;
}
