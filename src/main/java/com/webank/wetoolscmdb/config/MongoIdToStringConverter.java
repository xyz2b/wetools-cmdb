package com.webank.wetoolscmdb.config;

import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MongoIdToStringConverter implements Converter<Document, Document> {
    @Override
    public Document convert(Document document) {
        Object value = document.get(CiQueryConsist.QUERY_FILTER_ID);
        if(value == null) {
            return document;
        }

        if(value instanceof ObjectId) {
            ObjectId id = (ObjectId) value;
            document.put(CiQueryConsist.QUERY_FILTER_ID, id.toHexString());
            return document;
        }

        return document;
    }
}