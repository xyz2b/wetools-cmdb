package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;
import com.webank.wetoolscmdb.model.entity.mongo.ItsmProblemsDao;

import java.util.Date;
import java.util.List;

public interface ItsmProblemsService {
    int insertAll(List<ItsmProblemsResponse> itsmProblemsResponses);

    String findLastProblemCreateTime();
}
