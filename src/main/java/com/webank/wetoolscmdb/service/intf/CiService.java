package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;

public interface CiService {
    boolean createCi(Ci ci);
    boolean createCiMetaCollection(Ci ci);
    boolean existedCiMetaCollection(Ci ci);
}
