package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;

public interface FieldService {
    boolean createFieldCollection(Ci ci);
    boolean createField(Ci ci);
    boolean existedFieldMetaCollection(Ci ci);
}
