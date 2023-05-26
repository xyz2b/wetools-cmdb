package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.webank.wetoolscmdb.mapper.intf.mongo.CiDataRepository;
import com.webank.wetoolscmdb.model.dto.CiDataUpdate;
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
public class CiDataUpdateRepositoryImplTest {
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
    public void testUpdate() {
        Map<String, Object> map = new HashMap<>();
        map.put("dcn", "3");
        map.put("module", "333");
        Map<String, Object> filter = new HashMap<>();
        filter.put("guid", "100390024743222");

        CiDataUpdate ciDataUpdate = new CiDataUpdate(map, filter);
        List<CiDataUpdate> ciDataUpdateList = new ArrayList<>();
        ciDataUpdateList.add(ciDataUpdate);

        System.out.println(ciDataRepository.update("wb_host", "uat", ciDataUpdateList, false, false));
    }

    @Test
    public void testGetData() {
        String ciName = "wb_host";
        String env = "uat";
        Map<String, Object> filter = new HashMap<>();
//        Map<String, String> dataRange = new HashMap<>();
//        dataRange.put(">", "2023-05-23");
//        dataRange.put("<", "2023-05-24");
//        filter.put("created_date", dataRange);
//        filter.put("host_lanip", "172.21.7.40");
        filter.put("_id", "646f099de87e016baf640686");
        List<String> resultColumn = new ArrayList<>();
        resultColumn.add("host_lanip");
        resultColumn.add("dcn");
        List<Map<String, Object>> r = ciDataRepository.getData(ciName, env, filter, resultColumn);
        for(Map<String, Object> m : r) {
            System.out.println(m);
        }
    }

    @Test
    public void testUpdateBatch() {
        String ciName = "wb_host";
        String env = "uat";
        List<CiDataUpdate> ciDataUpdateList = new ArrayList<>();

        Map<String, Object> filter1 = new HashMap<>();
        filter1.put("_id", "646f099de87e016baf640686");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("module", "555");
        data1.put("dcn", "55");
        CiDataUpdate ciDataUpdate1 = new CiDataUpdate(data1, filter1);
        ciDataUpdateList.add(ciDataUpdate1);

        Map<String, Object> filter2 = new HashMap<>();
        filter2.put("_id", "646f099de87e016baf640400");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("module", "333");
        data2.put("dcn", "3");
        CiDataUpdate ciDataUpdate2 = new CiDataUpdate(data2, filter2);
        ciDataUpdateList.add(ciDataUpdate2);

        System.out.println(ciDataRepository.update(ciName, env, ciDataUpdateList, false, false));
    }
}