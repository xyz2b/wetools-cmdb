package com.webank.wetoolscmdb.cron;

import com.webank.wetoolscmdb.service.intf.CiService;
import com.webank.wetoolscmdb.service.intf.CmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;

// TODO: 定期同步ITSM问题单，根据上一次同步问题单中的最晚创建时间作为时间节点一直到当前时间，根据创建时间进行过滤。如果当前没有同步过问题单，库里的记录数为0，就同步当前时间这一年内的问题单
public class SyncItsmProblemsDataProcessor implements BasicProcessor {
    @Override
    public ProcessResult process(TaskContext taskContext) throws Exception {
        return null;
    }
}
