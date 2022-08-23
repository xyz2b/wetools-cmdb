package com.webank.wetoolscmdb.mapper.impl;

import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import com.webank.wetoolscmdb.service.impl.CiServiceImpl;
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
        System.out.println(ciRepository.getCiCollection("1111"));
    }
}
