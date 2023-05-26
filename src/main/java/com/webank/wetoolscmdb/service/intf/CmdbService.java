package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiRequest;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.dto.CiFieldCreateRequest;

import java.util.List;
import java.util.Map;

public interface CmdbService {
    List<CiField> getCmdbCiAllField(String ciName, String env);

    List<CiField> getCmdbCiField(String ciName, List<String> fieldName, String env);

    void syncManyColumnCmdbDataByFilterAsyncAndRegisterCron(CiRequest ciRequest);

    int syncManyColumnCmdbDataByFilter(Ci ci, Map<String, Object> filter);

    int getCmdbDataAllCount(String type, String env);
    int getCmdbDataCountByFilter(String type, Map<String, Object> filter, String env);

    void syncManyColumnCmdbDataAsync(CiFieldCreateRequest ciFieldCreateRequest);
}
