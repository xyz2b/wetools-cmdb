package com.webank.wetoolscmdb.mapper.impl;

import com.webank.wetoolscmdb.model.entity.mongo.Ci;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CiRepositoryTest {
    @Autowired
    CiRepository ciRepository;

    @Test
    public void testSaveCi() {
        Ci ci = new Ci();
        ci.setCnName("应用域");
        ci.setEnName("wb_Applicationdomain_111");
        ci.setCmdb(true);
        ci.setDelete(false);

        ciRepository.saveCi(ci);
    }
}
