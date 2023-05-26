package com.webank.wetoolscmdb.utils;

import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MongoQueryUtil {
    public static Query makeQueryByFilter(Map<String, Object> filter) throws RuntimeException {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        if(filter != null && filter.size() > 0) {
            for(Map.Entry<String, Object> e : filter.entrySet()) {
                if(e.getValue() instanceof String) {
                    String valueString = (String) e.getValue();
                    String[] values = valueString.split(",");
                    if(e.getKey().equals(CiQueryConsist.QUERY_FILTER_ID)) {
                        ObjectId[] idValues = new ObjectId[values.length];
                        for(int i = 0; i < values.length; i++) {
                            String value = values[i];
                            if(value != null) {
                                idValues[i] = new ObjectId(value);
                            } else {
                                throw new RuntimeException("_id must not be null");
                            }
                        }
                        criteriaList.add(Criteria.where(e.getKey()).in(Arrays.asList(idValues)));
                        continue;
                    }
                    criteriaList.add(Criteria.where(e.getKey()).in(Arrays.asList(values)));
                } else if(e.getValue() instanceof Map) {
                    Map<String, String> dateRange = (Map<String, String>) e.getValue();
                    for(Map.Entry<String, String> entry : dateRange.entrySet()) {
                        switch (entry.getKey()) {
                            case ">":
                                criteriaList.add(Criteria.where(e.getKey()).gt(entry.getValue()));
                                break;
                            case "<":
                                criteriaList.add(Criteria.where(e.getKey()).lt(entry.getValue()));
                                break;
                            case ">=":
                                criteriaList.add(Criteria.where(e.getKey()).gte(entry.getValue()));
                                break;
                            case "<=":
                                criteriaList.add(Criteria.where(e.getKey()).lte(entry.getValue()));
                                break;
                            default:
                                throw new RuntimeException("date range operator is must be < <= > >=");
                        }
                    }
                } else {
                    criteriaList.add(Criteria.where(e.getKey()).is(e.getValue()));
                }
            }
        }
        Criteria criteriaData = new Criteria();
        if(criteriaList.size() > 0) {
            criteriaData.andOperator(criteriaList);
        }
        query.addCriteria(criteriaData);
        return query;
    }

    public static Query makeQueryByFilter(Map<String, Object> filter, List<String> resultColumn) throws RuntimeException {
        Query query = makeQueryByFilter(filter);
        if(resultColumn != null && resultColumn.size() > 0) {
            for(String column : resultColumn) {
                query.fields().include(column);
            }
            query.fields().include(CiQueryConsist.QUERY_FILTER_ID);
        }
        return query;
    }

    public static Update makeUpdate(Map<String, Object> data) {
        Update update = new Update();
        for(Map.Entry<String, Object> entry : data.entrySet()) {
            update.set(entry.getKey(), entry.getValue());
        }

        return update;
    }
}
