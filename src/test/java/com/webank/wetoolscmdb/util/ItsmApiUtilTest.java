package com.webank.wetoolscmdb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wetoolscmdb.config.ItsmApiProperties;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsRequest;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;
import com.webank.wetoolscmdb.model.entity.mongo.ItsmProblemsDao;
import com.webank.wetoolscmdb.service.intf.ItsmProblemsService;
import com.webank.wetoolscmdb.utils.cmdb.CmdbApiUtil;
import com.webank.wetoolscmdb.utils.itsm.ItsmApiUtil;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItsmApiUtilTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_YEAR);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);


    @Autowired
    ItsmApiUtil itsmApiUtil;

    @Autowired
    ItsmApiProperties properties;

    @Autowired
    ItsmProblemsService itsmProblemsService;

    @Test
    public void testGetProblems() throws JsonProcessingException, ParseException {
        Date now = new Date();
        String nowYear = SIMPLE_DATE_FORMAT_YEAR.format(now);
        String nowYearFirstDayTime = nowYear + "-01-01 00:00:00";

        List<ItsmProblemsResponse> itsmProblemsResponses = itsmApiUtil.getProblemsBySolveTeamAndCreateTime(properties.getOtpdTeamIds(), nowYearFirstDayTime, SIMPLE_DATE_FORMAT_SECOND.format(now));

        itsmProblemsService.insertAll(itsmProblemsResponses);

    }
}