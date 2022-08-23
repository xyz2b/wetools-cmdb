package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;

import java.util.List;

public interface FiledService {
    boolean createFieldCollection(Ci ci);
    boolean createField(Ci ci);
    boolean existedFieldMetaCollection(Ci ci);
}
