package com.webank.wetoolscmdb.service.impl;

import com.mongodb.client.MongoCollection;
import com.webank.wetoolscmdb.mapper.intf.mongo.FiledRepository;
import com.webank.wetoolscmdb.model.dto.CiFiled;
import com.webank.wetoolscmdb.service.intf.FiledService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FieldServiceImpl implements FiledService {
    @Autowired
    FiledRepository filedRepository;

    @Override
    public boolean createFiled(List<CiFiled> filed, String belongCiId, String env) {

        // 创建元数据集合
        MongoCollection<Document> ciCollection = filedRepository.createFiledCollection(env);
        if (ciCollection == null) {
            log.error("create field collection failed: " + env);
            return false;
        }

        return true;
    }
}
