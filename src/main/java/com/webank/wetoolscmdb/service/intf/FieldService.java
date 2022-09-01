package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;

import java.util.List;

public interface FieldService {
    boolean createFieldCollection(Ci ci);
    List<CiField> insertAllField(Ci ci);
    boolean existedFieldMetaCollection(Ci ci);

    List<String> findCiAllFieldName(String ciName, String env);

    List<String> findCiAllCmdbFieldName(String ciName, String env);

    List<CiField> defaultCmdbCiFields();

    boolean deleteField(String ciName, String env, String fieldName);

    long deleteCiAllField(String ciName, String env);
}
