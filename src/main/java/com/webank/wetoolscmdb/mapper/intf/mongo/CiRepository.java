package com.webank.wetoolscmdb.mapper.intf.mongo;

import com.webank.wetoolscmdb.model.entity.mongo.Ci;

public interface CiRepository {
    void saveCi(Ci ci);
}
