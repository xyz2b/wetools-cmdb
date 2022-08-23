package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;

import java.util.List;
import java.util.Map;

public interface CmdbService {
    List<CiField> getCmdbCiAllField(Ci ci);

    // 返回同步成功的记录数
    void syncCmdbAllDataAsync(String type);
    void syncManyColumnCmdbAllDataAsync(String type, List<String> resultColumn);

    int syncManyColumnCmdbDataByFilter(String type, Map<String, String> filter, List<String> resultColumn);

    int getCmdbDataAllCount(String type);
    int getCmdbDataCountByFilter(String type, Map<String, String> filter);
}
