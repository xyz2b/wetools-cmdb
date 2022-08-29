package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;

import java.util.List;
import java.util.Map;

public interface CiDataService {
    boolean existedCiDataCollection(Ci ci);
    boolean createCiDataCollection(Ci ci);

    int insertCiData(Ci ci, List<Map<String, Object>> data);

    int updateCmdbCiData(Ci ci, List<Map<String, Object>> data);
    int updateCiData(Ci ci, List<Map<String, Object>> data);

    String getLastUpdateTime(String ciName, String env);

    long updateAll(String ciName, String env, Map<String, Object> data);
}
