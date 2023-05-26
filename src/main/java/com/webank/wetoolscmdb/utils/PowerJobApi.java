package com.webank.wetoolscmdb.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.constant.consist.PowerJobConsist;
import com.webank.wetoolscmdb.cron.SyncCmdbDataProcessor;
import com.webank.wetoolscmdb.model.dto.CiRequest;
import com.webank.wetoolscmdb.service.intf.CronService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.common.enums.ExecuteType;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.common.request.http.SaveJobInfoRequest;

import java.io.IOException;
import java.util.ArrayList;

@Component
@Slf4j
public class PowerJobApi {
    @Autowired
    CronService cronService;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public Long createCronJob(String jobName, String jobDescription, TimeExpressionType timeExpressionType, String timeExpression, ExecuteType executeType, ProcessorType processorType,
                                     String processor, Object jobParam) {
        SaveJobInfoRequest request = new SaveJobInfoRequest();
        request.setJobName(jobName);
        request.setJobDescription(jobDescription);

        String jobParamJson = null;
        try {
            jobParamJson = objectMapper.writeValueAsString(jobParam);
        } catch (JsonGenerationException e) {
            log.error("cron job param to json failed! ", e);
        } catch (JsonMappingException e) {
            log.error("cron job param to json failed! ", e);
        } catch (IOException e) {
            log.error("cron job param to json failed! ", e);
        }

        if (jobParamJson == null) {
            log.error("job: [{}], cron job is create failed, because job param json dump failed ", jobName);
            return null;
        } else {
            request.setJobParams(jobParamJson);
        }

        request.setTimeExpressionType(timeExpressionType);
        request.setTimeExpression(timeExpression);

        request.setExecuteType(executeType);
        request.setProcessorType(processorType);

        request.setMaxInstanceNum(1);
        request.setConcurrency(1);

        request.setInstanceTimeLimit((long) 0);
        request.setInstanceRetryNum(0);
        request.setTaskRetryNum(1);
        request.setMinCpuCores(0);
        request.setMinMemorySpace(0);
        request.setMinDiskSpace(0);
        request.setMaxWorkerCount(0);
        request.setEnable(true);
        request.setDesignatedWorkers("");
        request.setNotifyUserIds(new ArrayList<>(0));

        request.setProcessorInfo(SyncCmdbDataProcessor.class.getName());

        return cronService.createJob(request);
    }

    public boolean disableCronJob(Long jobId) {
        return cronService.disableJob(jobId);
    }
}
