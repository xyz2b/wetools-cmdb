package com.webank.wetoolscmdb.service.impl;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.constant.consist.*;
import com.webank.wetoolscmdb.cron.SyncCmdbDataProcessor;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiRequest;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.dto.CiFieldCreateRequest;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponse;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseDataHeader;
import com.webank.wetoolscmdb.service.intf.*;
import com.webank.wetoolscmdb.utils.PowerJobApi;
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
import java.util.*;


@Service
@Slf4j
public class CmdbServiceImpl implements CmdbService {
    @Autowired
    CmdbApiUtil cmdbApiUtil;
    @Autowired
    CiService ciService;

    @Autowired
    PowerJobApi powerJobApi;

    @Autowired
    CiDataService ciDataService;

    @Autowired
    FieldService fieldService;

    @Override
    public List<CiField> getCmdbCiAllField(String ciName, String env) {

        Map<String, CmdbResponseDataHeader> cmdbCiFieldAttributes = cmdbApiUtil.getCiFiledAttributes(ciName, env);

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
                log.warn("unknown cmdb response data field type, code: [{}], field_name: [{}], type: [{}]", WetoolsExceptionCode.UNKNOWN_CMDB_TYPE_ERROR, cmdbResponseDataHeaderEntry.getKey(), cmdbResponseDataHeader.getDataType());
                continue;
            }
            ciFieldList.add(ciField);
        }

        return ciFieldList;
    }

    @Override
    public List<CiField> getCmdbCiField(String ciName, List<String> fieldName, String env) {

        Map<String, CmdbResponseDataHeader> cmdbCiFieldAttributes = cmdbApiUtil.getCiFiledAttributes(ciName, fieldName, env);

        List<CiField> ciFieldList = new ArrayList<>(cmdbCiFieldAttributes.size());

        for(Map.Entry<String, CmdbResponseDataHeader> cmdbResponseDataHeaderEntry : cmdbCiFieldAttributes.entrySet()) {
            CiField ciField = new CiField();

            CmdbResponseDataHeader cmdbResponseDataHeader = cmdbResponseDataHeaderEntry.getValue();

            String envName = cmdbResponseDataHeaderEntry.getKey();
            // 跳过CMDB默认都会返回的字段
            if(envName.equals(CmdbApiConsist.QUERY_FILTER_UPDATED_DATE) || envName.equals(CmdbApiConsist.QUERY_FILTER_CREATED_DATE)
                    || envName.equals(CmdbApiConsist.QUERY_FILTER_UPDATED_BY) || envName.equals(CmdbApiConsist.QUERY_FILTER_CREATED_BY)
                    || envName.equals(CmdbApiConsist.QUERY_FILTER_GUID)) {
                continue;
            }

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
                log.warn("unknown cmdb response data field type, code: [{}], field_name: [{}], type: [{}]", WetoolsExceptionCode.UNKNOWN_CMDB_TYPE_ERROR, cmdbResponseDataHeaderEntry.getKey(), cmdbResponseDataHeader.getDataType());
                continue;
            }
            ciFieldList.add(ciField);
        }

        return ciFieldList;
    }

    // 新增CMDB字段时候进行同步使用，异步同步增量数据
    @Override
    public void syncManyColumnCmdbDataAsync(CiFieldCreateRequest ciFieldCreateRequest) {
        CallbackTaskScheduler.add(new CallbackTask<Integer>() {
            @Override
            public Integer execute() throws Exception {
                String ciName = ciFieldCreateRequest.getCiName();
                String env = ciFieldCreateRequest.getEnv();
                Map<String, Object> filter = ciFieldCreateRequest.getFilter();
                if(filter == null) {
                    filter = new HashMap<>(0);
                }

                // 数据库中没有对应的CI元信息
                if(ciService.isUpdating(ciName, env) == null) {
                    return -2;
                }
                // 只有CI不是updating状态才可以去更新同步
                // TODO: 需要一把分布式锁
                if(ciService.isUpdating(ciName, env)) {
                    return -1;
                }

                // 查询该CI所有的Field，拿出是CMDB的字段
                List<String> resultColumn = fieldService.findCiAllCmdbFieldName(ciName, env);

                // 从CMDB同步数据
                int successUpdateSum = 0;
                int startIndex = 0;
                CmdbResponse cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(ciName, filter, resultColumn, startIndex, env);
                List<Map<String, Object>> cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                successUpdateSum += ciDataService.updateCmdbCiDataByGuid(ciName, env, cmdbData);

                while (!cmdbApiUtil.isLastPage(cmdbResponse)) {
                    cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(ciName, filter, resultColumn, cmdbApiUtil.nextIndex(cmdbResponse), env);
                    cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                    successUpdateSum += ciDataService.updateCmdbCiDataByGuid(ciName, env, cmdbData);
                }

                return successUpdateSum;
            }

            @Override
            public void onSuccess(Integer syncSuccessDataCount) {
                String ciName = ciFieldCreateRequest.getCiName();
                String env = ciFieldCreateRequest.getEnv();
                Map<String, Object> filter = ciFieldCreateRequest.getFilter();
                if(filter == null) {
                    filter = new HashMap<>(0);
                }

                if (syncSuccessDataCount == -1) {
                    log.info("type: [{}], env: [{}] is updating!!!, give up this sync.", ciName, env);
                    return;
                } else if (syncSuccessDataCount == -2) {
                    log.error("type: [{}], env: [{}] ci metadata is not existed", ciName, env);
                    return;
                }

                int totalDataCount = getCmdbDataCountByFilter(ciName, filter, env);
                if (syncSuccessDataCount < totalDataCount) {
                    log.warn("sync data from cmdb failed, type: [{}], env: [{}], sync success: [{}], total: [{}]", ciName, env, syncSuccessDataCount, totalDataCount);
                } else {
                    log.info("sync data from cmdb success, type: [{}], env: [{}], sync success: [{}], total: [{}]", ciName, env, syncSuccessDataCount, totalDataCount);
                    // 更新CI的最后更新时间(ci_data_last_update_date)为通过来的CMDB数据中更新时间最晚的，直接对DB中数据的更新时间进行排序取最晚的一个
                    String lastUpdateTime = ciDataService.getLastUpdateTime(ciName, env);
                    if (lastUpdateTime != null) {
                        ciService.updateLastUpdateTime(ciName, env, lastUpdateTime);
                    } else {
                        log.error("type: [{}], env: [{}], update ci metadata last update time failed!!!", ciName, env);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                String ciName = ciFieldCreateRequest.getCiName();
                String env = ciFieldCreateRequest.getEnv();
                log.error("sync data from cmdb job error, type: [{}], env: [{}], message: [{}]",ciName, env, t);
            }
        });
    }

    // 首次创建CMDB CI时候进行同步使用，异步同步增量数据并注册定时任务
    @Override
    public void syncManyColumnCmdbDataByFilterAsyncAndRegisterCron(CiRequest ciRequest) {
        CallbackTaskScheduler.add(new CallbackTask<Integer>() {
            @Override
            public Integer execute() throws Exception {
                String ciName = ciRequest.getEnName();
                String env = ciRequest.getEnv();
                Map<String, Object> filter = ciRequest.getFilter();
                if(filter == null) {
                    filter = new HashMap<>(0);
                }

                // 数据库中没有对应的CI元信息
                if(ciService.isUpdating(ciName, env) == null) {
                    return -2;
                }
                // 只有CI不是updating状态才可以去更新同步
                // TODO: 需要一把分布式锁
                if(ciService.isUpdating(ciName, env)) {
                    return -1;
                }

                List<String> resultColumn = new ArrayList<>();
                for(CiField ciField : ciRequest.getFieldList()) {
                    if(ciField.getEnName() == null) {
                        continue;
                    }
                    if(ciField.getIsCmdb()) {
                        resultColumn.add(ciField.getEnName());
                    }
                }

                // sync many column cmdb all data
                // 从CMDB同步数据
                int successInsertSum = 0;
                int startIndex = 0;
                CmdbResponse cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(ciName, filter, resultColumn, startIndex, env);
                List<Map<String, Object>> cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                successInsertSum += ciDataService.updateCmdbCiDataByGuid(ciName, env, cmdbData);

                while (!cmdbApiUtil.isLastPage(cmdbResponse)) {
                    cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(ciName, filter, resultColumn, cmdbApiUtil.nextIndex(cmdbResponse), env);
                    cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                    successInsertSum += ciDataService.updateCmdbCiDataByGuid(ciName, env, cmdbData);
                }

                return successInsertSum;
            }

            @Override
            public void onSuccess(Integer syncSuccessDataCount) {
                String ciName = ciRequest.getEnName();
                String env = ciRequest.getEnv();
                Map<String, Object> filter = ciRequest.getFilter();
                if(filter == null) {
                    filter = new HashMap<>(0);
                }

                if (syncSuccessDataCount == -1) {
                    log.info("type: [{}], env: [{}] is updating!!!, give up this sync.", ciName, env);
                    return;
                } else if (syncSuccessDataCount == -2) {
                    log.error("type: [{}], env: [{}] ci metadata is not existed", ciName, env);
                    return;
                }

                int totalDataCount = getCmdbDataCountByFilter(ciName, filter, env);
                if (syncSuccessDataCount < totalDataCount) {
                    log.warn("sync data from cmdb failed, type: [{}], env: [{}], sync success: [{}], total: [{}]", ciName, env, syncSuccessDataCount, totalDataCount);
                } else {
                    log.info("sync data from cmdb success, type: [{}], env: [{}], sync success: [{}], total: [{}]", ciName, env, syncSuccessDataCount, totalDataCount);
                    // 更新CI的最后更新时间(ci_data_last_update_date)为通过来的CMDB数据中更新时间最晚的，直接对DB中数据的更新时间进行排序取最晚的一个
                    String lastUpdateTime = ciDataService.getLastUpdateTime(ciName, env);
                    if (lastUpdateTime != null) {
                        ciService.updateLastUpdateTime(ciName, env, lastUpdateTime);
                    } else {
                        log.error("type: [{}], env: [{}], update ci metadata last update time failed!!!", ciName, env);
                    }

                    // 定时增量同步CMDB数据，周期为 ci.getSynCmdbCycle()，向定时任务组件注册定时任务
                    Long cronId = ciService.getCiSyncCmdbCronId(ciName, env);
                    if (cronId != null && cronId == -1) {
                        CiRequest jobParam = new CiRequest();
                        jobParam.setEnv(ciRequest.getEnv());
                        jobParam.setEnName(ciRequest.getEnName());
                        jobParam.setIsCmdb(ciRequest.getIsCmdb());
                        jobParam.setSynCmdbCycle(ciRequest.getSynCmdbCycle());
                        jobParam.setFilter(ciRequest.getFilter());

                        Long cronJobId = powerJobApi.createCronJob(PowerJobConsist.CMDB_SYNC_JOB_NAME_PREFIX + ciName + "_" + env,
                                PowerJobConsist.CMDB_SYNC_JOB_NAME_PREFIX + ciName + "_" + env,
                                TimeExpressionType.FIXED_RATE,
                                String.valueOf(ciRequest.getSynCmdbCycle()),
                                ExecuteType.STANDALONE,
                                ProcessorType.BUILT_IN,
                                SyncCmdbDataProcessor.class.getName(),
                                jobParam);

                        if(cronJobId == null) {
                            log.error("type: [{}], env: [{}], cron job is create failed, because power job api return failed ", ciName, env);
                        } else {
                            if(ciService.updateCiSyncCmdbCronId(ciName, env, cronJobId)) {
                                log.info("type: [{}], env: [{}], cron job is create success, cronId: [{}] ", ciName, env, cronJobId);
                            } else {
                                log.error("type: [{}], env: [{}], cron job id write to db failed, but cron job is create success, cronId: [{}]", ciName, env, cronJobId);
                            }
                        }
                    } else if(cronId != null && cronId != -1) {
                        log.info("type: [{}], env: [{}], cron job is already existed, cronId: [{}] ", ciName, env, cronId);
                    } else {
                        log.error("type: [{}], env: [{}], cronId field is not existed!!!", ciName, env);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                String type = ciRequest.getEnName();
                String env = ciRequest.getEnv();
                log.error("sync data from cmdb job error, type: [{}], env: [{}], message: [{}]",type, env, t);
            }
        });
    }

    // 定时任务同步CMDB CI使用，同步进行增量数据的同步
    @Override
    public int syncManyColumnCmdbDataByFilter(Ci ci, Map<String, Object> filter) {
        String ciName = ci.getEnName();
        String env = ci.getEnv();

        // 数据库中没有对应的CI元信息
        if(ciService.isUpdating(ciName, env) == null) {
            log.error("type: [{}], env: [{}] ci metadata is not existed", ciName, env);
            return -2;
        }
        // 只有CI不是updating状态才可以去更新同步
        // TODO: 需要一把分布式锁
        if(ciService.isUpdating(ciName, env)) {
            log.info("type: [{}], env: [{}] is updating!!!, give up this sync", ciName, env);
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
        CmdbResponse cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(ciName, filter, resultColumn, startIndex, env);
        if(cmdbResponse.getHeaders().getContentRows() != 0) {
            List<Map<String, Object>> cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
            successUpdateSum += ciDataService.updateCmdbCiDataByGuid(ciName, env, cmdbData);

            while (!cmdbApiUtil.isLastPage(cmdbResponse)) {
                cmdbResponse = cmdbApiUtil.getCiDataByStartIndex(ciName, filter, resultColumn, cmdbApiUtil.nextIndex(cmdbResponse), env);
                if(cmdbResponse.getHeaders().getContentRows() != 0) {
                    cmdbData = cmdbApiUtil.parseCmdbResponseData(cmdbResponse.getData());
                    successUpdateSum += ciDataService.updateCmdbCiDataByGuid(ciName, env, cmdbData);
                }
            }
        }

        // 更新CI的最后更新时间(ci_data_last_update_date)为通过来的CMDB数据中更新时间最晚的，直接对DB中数据的更新时间进行排序取最晚的一个
        String lastUpdateTime = ciDataService.getLastUpdateTime(ciName, env);
        if (lastUpdateTime != null) {
            ciService.updateLastUpdateTime(ciName, env, lastUpdateTime);
        } else {
            log.error("type: [{}], env: [{}], update ci metadata last update time failed!!!", ciName, env);
        }

        return successUpdateSum;
    }

    @Override
    public int getCmdbDataAllCount(String type, String env) {
        return cmdbApiUtil.getCiDataCount(type, env);
    }

    @Override
    public int getCmdbDataCountByFilter(String type, Map<String, Object> filter, String env) {
        return cmdbApiUtil.getCiDataCount(type, filter, env);
    }
}
