package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiRequest;

public interface CiService {
    Ci insertOneCi(CiRequest ci);
    boolean createCiMetaCollection(String env);
    boolean existedCiMetaCollection(String env);
    boolean existedCi(String ciName, String env);
    Boolean isUpdating(String ciName, String env);
    Boolean updating(Ci ci);
    Boolean updated(Ci ci);
    Ci findCi(String ci_name, String env);
    Long getCiSyncCmdbCronId(String ci_name, String env);

    boolean updateCiSyncCmdbCronId(String ci_name, String env, Long cronId);

    boolean updateLastUpdateTime(String ciName, String env, String lastUpdateTime);
    String getLastUpdateTime(String ciName, String env, String lastUpdateTime);

    boolean deleteCi(String ciName, String env);
    boolean deleteCiPhysics(String ciName, String env);
}
