package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;

import java.util.List;
import java.util.Map;

public interface CmdbService {
    List<CiField> getCmdbCiAllField(Ci ci);

    List<CiField> getCmdbCiField(String ciName, List<String> fieldName, String env);

    // 返回同步成功的记录数
    void syncCmdbAllDataAsync(Ci ci);
    void syncManyColumnCmdbAllDataAsyncAndRegisterCron(Ci ci);

    int syncManyColumnCmdbDataByFilter(Ci ci, Map<String, Object> filter);

    int getCmdbDataAllCount(String type, String env);
    int getCmdbDataCountByFilter(String type, Map<String, Object> filter, String env);

    void syncManyColumnCmdbDataAsync(Ci ci);
}
