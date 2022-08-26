package com.webank.wetoolscmdb.mapper.impl.mongo;

import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class FieldRepositoryImplTest {
    @Autowired
    FieldRepository fieldRepository;

    @Test
    void testFindCiAllFieldName() {
        System.out.println(fieldRepository.findCiAllFieldName("wb_host", "uat"));
    }
}