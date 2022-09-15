package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;

import java.util.List;

public interface ItsmProblemsService {
    int insertAll(List<ItsmProblemsResponse> itsmProblemsResponses);
}
