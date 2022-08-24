package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;

import java.util.List;

public interface CiService {
    boolean createCi(Ci ci);
    boolean createCiMetaCollection(Ci ci);
    boolean existedCiMetaCollection(Ci ci);
    boolean existedCi(Ci ci);
    boolean isUpdating(Ci ci);
    Ci findCi(String ci_name, String env);
    Long getCiSyncCmdbCronId(String ci_name, String env);

    boolean updateCiSyncCmdbCronId(String ci_name, String env, Long cronId);
}
