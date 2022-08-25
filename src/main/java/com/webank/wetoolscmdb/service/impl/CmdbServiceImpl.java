package com.webank.wetoolscmdb.service.impl;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.constant.consist.CiFiledType;
import com.webank.wetoolscmdb.constant.consist.CmdbQueryResponseDataType;
import com.webank.wetoolscmdb.constant.consist.PowerJobConsist;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.cron.SyncCmdbDataProcessor;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponse;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseData;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseDataHeader;
import com.webank.wetoolscmdb.service.intf.CiDataService;
import com.webank.wetoolscmdb.service.intf.CiService;
import com.webank.wetoolscmdb.service.intf.CmdbService;
import com.webank.wetoolscmdb.service.intf.CronService;
import com.webank.wetoolscmdb.utils.cmdb.CmdbApiUtil;
import com.webank.wetoolscmdb.utils.cocurrent.CallbackTask;
import com.webank.wetoolscmdb.utils.cocurrent.CallbackTaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.powerjob.common.enums.ExecuteType;
import tech.powerjob.common.enums.ProcessorType;
import tech.powerjob.common.enums.TimeExpressionType;
import tech.powerjob.common.request.http.SaveJobInfoRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CmdbServiceImpl implements CmdbService {
    @Autowired
    CmdbApiUtil cmdbApiUtil;
    @Autowired
    CiService ciService;
    @Autowired
    CronService cronService;
    @Autowired
    CiDataService ciDataService;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<CiField> getCmdbCiAllField(Ci ci) {

        Map<String, CmdbResponseDataHeader> cmdbCiFieldAttributes = cmdbApiUtil.getCiFiledAttributes(ci.getEnName());

        List<CiField> ciFieldList = new ArrayList<>(cmdbCiFieldAttributes.size());

        for(Map.Entry<String, CmdbResponseDataHeader> cmdbResponseDataHeaderEntry : cmdbCiFieldAttributes.entrySet()) {
            CiField ciField = new CiField();

            CmdbResponseDataHeader cmdbResponseDataHeader = cmdbResponseDataHeaderEntry.getValue();

            ciField.setEnName(cmdbResponseDataHeaderEntry.getKey());
            ciField.setCnName(cmdbResponseDataHeaderEntry.getValue().getName());
            ciField.setIsCmdb(true);
            ciField.setIsDisplay(cmdbResponseDataHeader.getDisplayType().equals("1"));

            if(cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.REF)) {
                ciField.setType(CiFiledType.STRING);
            } else if (cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.MULTI_REF)) {
                ciField.setType(CiFiledType.LIST);
            } else if (cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.SELECT)) {
                ciField.setType(CiFiledType.STRING);
            } else if (cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.TEXT)) {
                ciField.setType(CiFiledType.STRING);
            } else if (cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.TEXTAREA)) {
                ciField.setType(CiFiledType.STRING);
            } else if (cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.NUMBER)) {
                ciField.setType(CiFiledType.NUMBER);
            } else if (cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.HIDDEN)) {
                ciField.setType(CiFiledType.STRING);
            } else if (cmdbResponseDataHeader.getDataType().equals(CmdbQueryResponseDataType.DATE)) {
                ciField.setType(CiFiledType.DATE);
            } else {
                log.warn("unknown cmdb response data field type, code: [" + WetoolsExceptionCode.UNKNOWN_CMDB_TYPE_ERROR + "], field_name: " + cmdbResponseDataHeaderEntry.getKey() + "], type: [" + cmdbResponseDataHeader.getDataType() + "]");
                continue;
            }
            ciFieldList.add(ciField);
        }

        return ciFieldList;
    }

    @Override
    public void syncCmdbAllDataAsync(Ci ci) {
        CallbackTaskScheduler.add(new CallbackTask<Integer>() {
            @Override
            public Integer execute() throws Exception {
                String type = ci.getEnName();

                // sync cmdb all data
                // 数据库中没有对应的CI元信息
                if(ciService.isUpdating(ci) == null) {
                    return -2;
                }
                // 只有CI不是updating状态才可以去更新同步
                // TODO: 需要一把分布式锁
                if(ciService.isUpdating(ci)) {
                    return -1;
                }

                // 从CMDB同步数据
                int successInsertSum = 0;
                int startIndex = 0;
                CmdbResponse cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(type, startIndex);
                List<Map<String, Object>> cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                successInsertSum += ciDataService.insertCiData(ci, cmdbData);

                while (!cmdbApiUtil.isLastPage(cmdbResponse)) {
                    cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(type, cmdbApiUtil.nextIndex(cmdbResponse));
                    cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                    successInsertSum += ciDataService.insertCiData(ci, cmdbData);
                }

                return successInsertSum;
            }

            @Override
            public void onSuccess(Integer syncSuccessDataCount) {
                String type = ci.getEnName();
                String env = ci.getEnv();
                if (syncSuccessDataCount == -1) {
                    log.info("type " + type + ", env " + env +  " is updating!!!, give up this sync.");
                    return;
                } else if (syncSuccessDataCount == -2) {
                    log.error("type " + type + ", env " + env +  " ci metadata is not existed");
                    return;
                }
                int totalDataCount = getCmdbDataAllCount(type);
                if (syncSuccessDataCount < totalDataCount) {
                    log.warn("sync data from cmdb failed, type " + type + ", env " + env +  ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                } else {
                    log.info("sync data from cmdb success, type " + type + ", env " + env +  ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                    // TODO: 更新CI的最后更新时间(ci_data_last_update_date)为通过来的CMDB数据中更新时间最晚的，直接对DB中数据的更新时间进行排序取最晚的一个

                }
            }

            @Override
            public void onFailure(Throwable t) {
                String type = ci.getEnName();
                String env = ci.getEnv();
                log.error("sync data from cmdb job error, type " + type + ", env " + env + ", message " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void syncManyColumnCmdbAllDataAsync(Ci ci) {
        CallbackTaskScheduler.add(new CallbackTask<Integer>() {
            @Override
            public Integer execute() throws Exception {
                String type = ci.getEnName();

                // 数据库中没有对应的CI元信息
                if(ciService.isUpdating(ci) == null) {
                    return -2;
                }
                // 只有CI不是updating状态才可以去更新同步
                // TODO: 需要一把分布式锁
                if(ciService.isUpdating(ci)) {
                    return -1;
                }

                List<String> resultColumn = new ArrayList<>();
                for(CiField ciField : ci.getFieldList()) {
                    resultColumn.add(ciField.getEnName());
                }

                // sync many column cmdb all data
                // 从CMDB同步数据
                int successInsertSum = 0;
                int startIndex = 0;
                CmdbResponse cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(type, resultColumn, startIndex);
                List<Map<String, Object>> cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                successInsertSum += ciDataService.insertCiData(ci, cmdbData);

                while (!cmdbApiUtil.isLastPage(cmdbResponse)) {
                    cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(type, resultColumn, cmdbApiUtil.nextIndex(cmdbResponse));
                    cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                    successInsertSum += ciDataService.insertCiData(ci, cmdbData);
                }

                return successInsertSum;
            }

            @Override
            public void onSuccess(Integer syncSuccessDataCount) {
                String type = ci.getEnName();
                String env = ci.getEnv();

                if (syncSuccessDataCount == -1) {
                    log.info("type " + type + ", env " + env + " is updating!!!, give up this sync");
                    return;
                } else if (syncSuccessDataCount == -2) {
                    log.error("type " + type + ", env " + env +  " ci metadata is not existed");
                    return;
                }

                int totalDataCount = getCmdbDataAllCount(type);
                if (syncSuccessDataCount < totalDataCount) {
                    log.warn("sync data from cmdb failed, type " + type + ", env " + env + ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                } else {
                    log.info("sync data from cmdb success, type " + type + ", env " + env + ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                    // TODO: 更新CI的最后更新时间(ci_data_last_update_date)为通过来的CMDB数据中更新时间最晚的，直接对DB中数据的更新时间进行排序取最晚的一个

                    // 定时增量同步CMDB数据，周期为 ci.getSynCmdbCycle()，向定时任务组件注册定时任务
                    Long cronId = ciService.getCiSyncCmdbCronId(type, env);
                    if (cronId != null && cronId == -1) {
                        SaveJobInfoRequest request = new SaveJobInfoRequest();
                        request.setJobName(PowerJobConsist.CMDB_SYNC_JOB_NAME_PREFIX + type + "_" + env);
                        request.setJobDescription(PowerJobConsist.CMDB_SYNC_JOB_NAME_PREFIX + type + "_" + env);
                        Ci jobParam = new Ci();
                        jobParam.setEnv(ci.getEnv());
                        jobParam.setEnName(ci.getEnName());

                        String jobParamJson = null;
                        try {
                            jobParamJson = objectMapper.writeValueAsString(jobParam);
                        } catch (JsonGenerationException e) {
                            e.printStackTrace();
                        } catch (JsonMappingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (jobParamJson == null) {
                            log.error("type " + type + ", env " + env + ", cron job is create failed, because job param json dump failed");
                            return;
                        } else {
                            request.setJobParams(jobParamJson);
                        }

                        request.setTimeExpressionType(TimeExpressionType.FIXED_RATE);
                        request.setTimeExpression(String.valueOf(ci.getSynCmdbCycle()));

                        request.setExecuteType(ExecuteType.STANDALONE);
                        request.setProcessorType(ProcessorType.BUILT_IN);

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

                        Long cronJobId = cronService.createJob(request);

                        if(cronJobId == null) {
                            log.error("type " + type + ", env " + env + ", cron job is create failed, because power job api return failed");
                        } else {
                            if(ciService.updateCiSyncCmdbCronId(type, env, cronJobId)) {
                                log.info("type " + type + ", env " + env + ", cron job is create success, cronId " + cronJobId);
                            } else {
                                log.error("type " + type + ", env " + env + ", cron job id write to db failed, but cron job is create success, cronId " + cronJobId);
                            }
                        }
                    } else if(cronId != null && cronId != -1) {
                        log.info("type " + type + ", env " + env + ", cron job is already existed" + ", cronId " + cronId);
                    } else {
                        log.error("type " + type + ", env " + env + ", cronId field is not existed!!!");
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                String type = ci.getEnName();

                log.error("sync data from cmdb job error, type " + type + ", message " + t.getMessage());
            }
        });
    }


    @Override
    public int syncManyColumnCmdbDataByFilter(Ci ci, Map<String, Object> filter) {
        String type = ci.getEnName();
        String env = ci.getEnv();

        // 数据库中没有对应的CI元信息
        if(ciService.isUpdating(ci) == null) {
            log.error("type " + type + ", env " + env +  " ci metadata is not existed");
            return -2;
        }
        // 只有CI不是updating状态才可以去更新同步
        // TODO: 需要一把分布式锁
        if(ciService.isUpdating(ci)) {
            log.info("type " + type + ", env " + env + " is updating!!!, give up this sync");
            return -1;
        }

        // 查询该CI所有的Field，拿出是CMDB的字段，组成CI的Field填入CI的fieldList字段，只需要填充field的en_name即可
        List<String> resultColumn = new ArrayList<>();
        for(CiField ciField : ci.getFieldList()) {
            if(ciField.getIsCmdb()) {
                resultColumn.add(ciField.getEnName());
            }
        }

        // 从CMDB同步数据
        int successUpdateSum = 0;
        int startIndex = 0;
        CmdbResponse cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(type, filter, resultColumn, startIndex);
        List<Map<String, Object>> cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
        successUpdateSum += ciDataService.updateCiData(ci, cmdbData);

        while (!cmdbApiUtil.isLastPage(cmdbResponse)) {
            cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(type, filter, resultColumn, cmdbApiUtil.nextIndex(cmdbResponse));
            cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
            successUpdateSum += ciDataService.updateCiData(ci, cmdbData);
        }

        return successUpdateSum;
    }

    @Override
    public int getCmdbDataAllCount(String type) {
        return cmdbApiUtil.getCiDataCount(type);
    }

    @Override
    public int getCmdbDataCountByFilter(String type, Map<String, Object> filter) {
        return cmdbApiUtil.getCiDataCount(type, filter);
    }
}
