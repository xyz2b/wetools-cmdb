package com.webank.wetoolscmdb.service.intf;

import java.util.List;
import java.util.Map;

public interface CmdbService {
    void syncCmdbAllData(String type);
    void syncManyColumnCmdbData(String type, List<String> resultColumn);
    void syncManyColumnCmdbDataByFilter(String type, Map<String, String> filter, List<String> resultColumn);
}
