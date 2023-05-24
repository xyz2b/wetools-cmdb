package com.webank.wetoolscmdb.cron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbQueryDateFilter;
import com.webank.wetoolscmdb.service.intf.CiService;
import com.webank.wetoolscmdb.service.intf.CmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.common.utils.CommonUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class SyncCmdbDataProcessor implements BasicProcessor {
    @Autowired
    CmdbService cmdbService;

    @Autowired
    CiService ciService;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger omsLogger = context.getOmsLogger();

        omsLogger.info("SyncCmdbDataProcessor start process, context is {}.", context);

        // JobParams是需要进行CMDB同步的CI信息，只需要填充ci.en_name以及ci.env即可
        CommonUtils.requireNonNull(context.getJobParams(), "sync ci can't be empty!!!");

        Ci jobParams = objectMapper.readValue(context.getJobParams(), Ci.class);

        if(!ciService.existedCi(jobParams)) {
            return new ProcessResult(false, "env " + jobParams.getEnv() + ", ci " + jobParams.getEnName() + " is not existed");
        }

        // 查询CI，拿出上次最后更新时间，本次更新的过滤条件filter就是updateTime大于该时间
        Ci ci = ciService.findCi(jobParams.getEnName(), jobParams.getEnv());
        Map<String, Object> filter = ci.getFilter();
        if(filter == null) {
            filter = new HashMap<>(0);
        }
        CmdbQueryDateFilter cmdbQueryDateFilter = new CmdbQueryDateFilter();
        Map<String, String> query = new HashMap<>();
        query.put(CmdbApiConsist.QUERY_FILTER_GREATER_THAN, ci.getCiDataLastUpdateDate());
        cmdbQueryDateFilter.setRange(query);
        filter.put(CmdbApiConsist.QUERY_FILTER_UPDATED_DATE, cmdbQueryDateFilter);

        int syncSuccessCount = cmdbService.syncManyColumnCmdbDataByFilter(ci, filter);

        return new ProcessResult(true, "env " + jobParams.getEnv() + " ci " + jobParams.getEnName() + " sync success, total " + syncSuccessCount);
    }
}
