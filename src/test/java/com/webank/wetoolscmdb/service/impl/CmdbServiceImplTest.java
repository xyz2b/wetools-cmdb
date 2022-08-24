package com.webank.wetoolscmdb.service.impl;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.service.intf.CmdbService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CmdbServiceImplTest {
    @Autowired
    CmdbService cmdbService;

    @Test
    public void testGetCmdbCiAllField() {
        Ci ci = new Ci();
        ci.setEnName("wb_host");
        System.out.println(cmdbService.getCmdbCiAllField(ci));
    }
}