package com.webank.wetoolscmdb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wetoolscmdb.config.ItsmApiProperties;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsRequest;
import com.webank.wetoolscmdb.utils.cmdb.CmdbApiUtil;
import com.webank.wetoolscmdb.utils.itsm.ItsmApiUtil;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItsmApiUtilTest {
    @Autowired
    ItsmApiUtil itsmApiUtil;

    @Autowired
    ItsmApiProperties properties;

    @Test
    public void testGetProblems() throws JsonProcessingException {
        ItsmProblemsRequest itsmProblemsRequest = new ItsmProblemsRequest();
        List<Integer> handlerTeamIds = new ArrayList<>(properties.getOtpdTeamIds());
        itsmProblemsRequest.setHandlerTeamIds(handlerTeamIds);
        itsmProblemsRequest.setCurrentPage(1);
        itsmProblemsRequest.setPageSize(50);

        System.out.println(itsmApiUtil.getProblems(itsmProblemsRequest));
    }
}