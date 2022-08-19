package com.webank.wetoolscmdb.mapper.impl;

import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CiDaoRepositoryTest {
    @Autowired
    CiRepository ciRepository;

    @Test
    public void testSaveCi() {
        CiDao ciDao = new CiDao();
        ciDao.setCnName("应用域");
        ciDao.setEnName("wb_Applicationdomain_111");
        ciDao.setCmdb(true);
        ciDao.setDelete(false);

    }
}
