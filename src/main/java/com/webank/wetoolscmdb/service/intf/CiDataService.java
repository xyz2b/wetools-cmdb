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

    List<Map<String, Object>> getAllData(String ciName, String env);
    List<Map<String, Object>> getData(String ciName, String env, Map<String, Object> filter, List<String> resultColumn);

}
