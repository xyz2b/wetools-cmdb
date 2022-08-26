package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;

import java.util.List;

public interface FieldService {
    boolean createFieldCollection(Ci ci);
    List<CiField> insertAllField(Ci ci);
    boolean existedFieldMetaCollection(Ci ci);

    List<String> findCiAllFieldName(String ci_name, String env);
}
