package com.webank.wetoolscmdb.cron;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

public class SyncCmdbDataProcessor implements BasicProcessor {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger omsLogger = context.getOmsLogger();


        return null;
    }
}
