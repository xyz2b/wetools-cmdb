package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CiDataRepositoryImplTest {
    @Autowired
    CiDataRepository ciDataRepository;

    @Test
    public void testGetLastUpdateTime() {
        System.out.println(ciDataRepository.getLastUpdateTime("wb_host", "uat"));
    }

    @Test
    public void testFindOne() {
        Document document = ciDataRepository.findOneByGuid("wb_host", "uat", "11111");
        System.out.println(document.getString("host_assetid"));
    }

    @Test
    public void testSaveOne() {
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
    public void testUpdateCiData() {
        Map<String, Object> map = new HashMap<>();

    }

    @Test
    public void testUpdateAll() {
        Map<String, Object> map = new HashMap<>();
        map.put("test1", "111");
        map.put("test2", "222");
        ciDataRepository.updateAll("wb_host", "uat", map);
    }

    @Test
    public void testGetData() {
        String ciName = "wb_host";
        String env = "uat";
        Map<String, Object> filter = new HashMap<>();
        Map<String, String> dataRange = new HashMap<>();
        dataRange.put(">", "2023-05-23");
        dataRange.put("<", "2023-05-24");
        filter.put("created_date", dataRange);
        filter.put("host_lanip", "172.21.7.40");
        List<String> resultColumn = new ArrayList<>();
        resultColumn.add("host_lanip");
        resultColumn.add("dcn");
        List<Map<String, Object>> r = ciDataRepository.getData(ciName, env, filter, resultColumn);
        for(Map<String, Object> m : r) {
            System.out.println(m);
        }
    }
}