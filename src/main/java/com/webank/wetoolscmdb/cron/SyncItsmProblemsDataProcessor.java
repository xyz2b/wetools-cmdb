package com.webank.wetoolscmdb.cron;

import com.webank.wetoolscmdb.config.ItsmApiProperties;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;
import com.webank.wetoolscmdb.service.intf.ItsmProblemsService;
import com.webank.wetoolscmdb.utils.itsm.ItsmApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

// TODO: 一键配置SyncItsmProblemsDataProcessor定时任务的接口

//  定期同步ITSM问题单，根据上一次同步问题单中的最晚创建时间作为时间节点一直到当前时间，根据创建时间进行过滤。
//  如果当前没有同步过问题单，库里的记录数为0，就同步当前时间这一年内的问题单
@Component
public class SyncItsmProblemsDataProcessor implements BasicProcessor {
    @Autowired
    ItsmProblemsService itsmProblemsService;
    @Autowired
    ItsmApiUtil itsmApiUtil;
    @Autowired
    private ItsmApiProperties props;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_YEAR);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        String lastProblemCreateDate = itsmProblemsService.findLastProblemCreateTime();

        int count = 0;
        List<ItsmProblemsResponse> itsmProblemsResponses;
        Date now = new Date();
        if (lastProblemCreateDate == null) {
            String nowYear = SIMPLE_DATE_FORMAT_YEAR.format(now);
            String nowYearFirstDayTime = nowYear + "-01-01 00:00:00";
            itsmProblemsResponses = itsmApiUtil.getProblemsBySolveTeamAndCreateTime(props.getOtpdTeamIds(), nowYearFirstDayTime, SIMPLE_DATE_FORMAT_SECOND.format(now));
        } else {
            itsmProblemsResponses = itsmApiUtil.getProblemsBySolveTeamAndCreateTime(props.getOtpdTeamIds(), lastProblemCreateDate, SIMPLE_DATE_FORMAT_SECOND.format(now));
        }

        count = itsmProblemsResponses.size();

        if(count > 0) {
            itsmProblemsService.insertAll(itsmProblemsResponses);
        }

        return new ProcessResult(true, "sync itsm problem success, total: " + count);
    }
}
