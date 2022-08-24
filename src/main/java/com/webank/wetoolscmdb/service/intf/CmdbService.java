package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;

import java.util.List;
import java.util.Map;

public interface CmdbService {
    List<CiField> getCmdbCiAllField(Ci ci);

    // 返回同步成功的记录数
    void syncCmdbAllDataAsync(Ci ci);
    void syncManyColumnCmdbAllDataAsync(Ci ci);

    int syncManyColumnCmdbDataByFilter(Ci ci, Map<String, Object> filter);

    int getCmdbDataAllCount(String type);
    int getCmdbDataCountByFilter(String type, Map<String, Object> filter);
}
