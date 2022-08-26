package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CiDataRepositoryTest {
    @Autowired
    CiDataRepository ciDataRepository;

    @Test
    void testGetLastUpdateTime() {
        System.out.println(ciDataRepository.getLastUpdateTime("wb_host", "uat"));
    }

    @Test
    void testFindOne() {
        Document document = ciDataRepository.findOne("wb_host", "uat", "11111");
        System.out.println(document.getString("host_assetid"));
    }

    @Test
    void testSaveOne() {
        Document document = new Document();

        Map<String, Object> map = new HashMap<>();
        map.put("host_assetid", "111111111");
        map.put("host_lanip", "xxxxxxxxxxx");

        System.out.println(document);
        document.putAll(map);
        System.out.println(document);

        ciDataRepository.saveOne("wb_host", "uat", document);
    }

    @Test
    void testUpdateCiData() {
        Map<String, Object> map = new HashMap<>();

    }

    @Test
    void testUpdateAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("test1", "111");
        map.put("test2", "222");
        ciDataRepository.updateAll("wb_host", "uat", map);
    }
}