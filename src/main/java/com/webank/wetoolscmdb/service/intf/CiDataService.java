package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiDataUpdate;

import java.util.List;
import java.util.Map;

public interface CiDataService {
    boolean existedCiDataCollection(String ciName, String env);
    boolean createCiDataCollection(String ciName, String env);

    int insertCiData(String ciName, String env, List<Map<String, Object>> data);

    int updateCmdbCiDataByGuid(String ciName, String env, List<Map<String, Object>> data) throws RuntimeException;
    long updateCiData(Ci ci, List<CiDataUpdate> data) throws RuntimeException;

    String getLastUpdateTime(String ciName, String env);

    List<Map<String, Object>> getAllData(String ciName, String env);
    List<Map<String, Object>> getData(String ciName, String env, Map<String, Object> filter, List<String> resultColumn);

}
