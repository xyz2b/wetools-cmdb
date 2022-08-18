package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.model.entity.mongo.Ci;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class CiRepositoryImpl implements CiRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveCi(Ci ci) {
        mongoTemplate.save(ci);
    }
}
