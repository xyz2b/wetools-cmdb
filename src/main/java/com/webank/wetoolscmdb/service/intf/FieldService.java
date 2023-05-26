package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.CiField;
import org.bson.Document;

import java.util.List;

public interface FieldService {
    boolean createFieldCollection(String env);
    List<CiField> insertAllField(String ciName, String env, List<CiField> fieldList);
    boolean existedFieldMetaCollection(String env);

    List<String> findCiAllFieldName(String ciName, String env);

    List<String> findCiAllCmdbFieldName(String ciName, String env);

    List<Document> findCiFiled(String ciName, String env, List<String> resultColumn);

    List<CiField> defaultCmdbCiFields();

    List<CiField> defaultNoCmdbCiFields();

    int deleteField(String ciName, String env, List<String> fieldNames);

    long deleteCiAllField(String ciName, String env);
}
