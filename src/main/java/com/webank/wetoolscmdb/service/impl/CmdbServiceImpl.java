package com.webank.wetoolscmdb.service.impl;

import com.webank.wetoolscmdb.constant.consist.CiFiledType;
import com.webank.wetoolscmdb.constant.consist.CmdbQueryResponseDataType;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseDataHeader;
import com.webank.wetoolscmdb.service.intf.CmdbService;
import com.webank.wetoolscmdb.utils.cmdb.CmdbApiUtil;
import com.webank.wetoolscmdb.utils.cocurrent.CallbackTask;
import com.webank.wetoolscmdb.utils.cocurrent.CallbackTaskScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CmdbServiceImpl implements CmdbService {
    @Autowired
    CmdbApiUtil cmdbApiUtil;

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
            } else {
                log.warn("unknown cmdb response data field type, code: [" + WetoolsExceptionCode.UNKNOWN_CMDB_TYPE_ERROR + "], field_name: " + cmdbResponseDataHeaderEntry.getKey() + "], type: [" + cmdbResponseDataHeader.getDataType() + "]");
                continue;
            }
            ciFieldList.add(ciField);
        }

        return ciFieldList;
    }

    @Override
    public void syncCmdbAllDataAsync(String type) {
        CallbackTaskScheduler.add(new CallbackTask<Integer>() {

            @Override
            public Integer execute() throws Exception {
                // TODO: sync cmdb all data
                return 0;
            }

            @Override
            public void onSuccess(Integer syncSuccessDataCount) {
                int totalDataCount = getCmdbDataAllCount(type);
                if (syncSuccessDataCount < totalDataCount) {
                    log.warn("sync data from cmdb failed, type " + type + ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                } else {
                    log.info("sync data from cmdb success, type " + type + ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("sync data from cmdb job error, type " + type + ", message " + t.getMessage());
            }
        });
    }

    @Override
    public void syncManyColumnCmdbAllDataAsync(String type, List<String> resultColumn) {
        CallbackTaskScheduler.add(new CallbackTask<Integer>() {

            @Override
            public Integer execute() throws Exception {
                // TODO: sync many column cmdb all data
                return 0;
            }

            @Override
            public void onSuccess(Integer syncSuccessDataCount) {
                int totalDataCount = getCmdbDataAllCount(type);
                if (syncSuccessDataCount < totalDataCount) {
                    log.warn("sync data from cmdb failed, type " + type + ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                } else {
                    log.info("sync data from cmdb success, type " + type + ", sync success " + syncSuccessDataCount + ", total " + totalDataCount);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                log.error("sync data from cmdb job error, type " + type + ", message " + t.getMessage());
            }
        });
    }

    @Override
    public int syncManyColumnCmdbDataByFilter(String type, Map<String, String> filter, List<String> resultColumn) {

        return 0;
    }

    @Override
    public int getCmdbDataAllCount(String type) {
        return cmdbApiUtil.getCiDataCount(type);
    }

    @Override
    public int getCmdbDataCountByFilter(String type, Map<String, String> filter) {
        return cmdbApiUtil.getCiDataCount(type, filter);
    }
}
