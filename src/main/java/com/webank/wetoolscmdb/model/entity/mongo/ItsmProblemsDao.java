package com.webank.wetoolscmdb.model.entity.mongo;

import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Accessors(chain = true)
@ToString
public class ItsmProblemsDao {
    private static final long serialVersionUID = -3258839839160856613L;

    @Id
    @Field("_id")
    private String id;
    @Field("problem_id")
    private long problem_id;
    @Field("problem_title")
    private String problemTitle;
    @Field("problem_status")
    private String problemStatus;
    @Field("source_name")
    private String sourceName;
    @Field("priority_level")
    private String priorityLevel;
    @Field("created_date")
    private String createDate;
    @Field("plan_date")
    private String planDate;
    @Field("solve_date")
    private String solveDate;
    @Field("solve_user")
    private String solveUser;
    @Field("solve_user_name")
    private String solveUserName;
    @Field("solve_team_name")
    private String solveTeamName;
    @Field("solve_team_id")
    private int solveTeamId;


    public static ItsmProblemsDao transfer(ItsmProblemsResponse itsmProblemsResponse) {
        ItsmProblemsDao itsmProblemsDao = new ItsmProblemsDao();
        itsmProblemsDao.setProblem_id(itsmProblemsResponse.getId());
        itsmProblemsDao.setProblemTitle(itsmProblemsResponse.getProblemTitle());
        itsmProblemsDao.setProblemStatus(itsmProblemsResponse.getProblemStatus());
        itsmProblemsDao.setSourceName(itsmProblemsResponse.getSourceName());
        itsmProblemsDao.setPriorityLevel(itsmProblemsResponse.getPriorityLevel());

        itsmProblemsDao.setCreateDate(itsmProblemsResponse.getCreateDate());
        itsmProblemsDao.setPlanDate(itsmProblemsResponse.getPlanDate());
        itsmProblemsDao.setSolveDate(itsmProblemsResponse.getSolveDate());

        itsmProblemsDao.setSolveUser(itsmProblemsResponse.getSolveUser());
        itsmProblemsDao.setSolveUserName(itsmProblemsResponse.getSolveUserName());
        itsmProblemsDao.setSolveTeamName(itsmProblemsResponse.getSolveTeamName());
        itsmProblemsDao.setSolveTeamId(itsmProblemsResponse.getSolveTeamId());

        return itsmProblemsDao;
    }
}
