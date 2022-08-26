package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.service.impl.CiServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CiDaoRepositoryTest {
    @Autowired
    CiRepository ciRepository;

    @Test
    public void testSaveCi() {
        List<CiDao> ciDaoList = new ArrayList<>();
        CiDao ciDao = new CiDao();
        ciDao.setEnName("test_ci");
        ciDaoList.add(ciDao);

        ciRepository.insertAllCi(ciDaoList, "test");
    }

    @Test
    public void testFindCi() {
        CiDao ciDao = ciRepository.findCi("test", "uat");
        System.out.println(ciDao);
    }

    @Test
    public void testUpdateLastUpdateTime() {
        ciRepository.updateLastUpdateTime("wb_host", "uat", "2022-08-25 15:59:32:281");
    }


}
