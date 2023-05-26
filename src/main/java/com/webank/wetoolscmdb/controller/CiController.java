package com.webank.wetoolscmdb.controller;

import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.model.dto.*;
import com.webank.wetoolscmdb.service.intf.CiDataService;
import com.webank.wetoolscmdb.service.intf.CiService;
import com.webank.wetoolscmdb.service.intf.CmdbService;
import com.webank.wetoolscmdb.service.intf.FieldService;
import com.webank.wetoolscmdb.utils.PowerJobApi;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping(path = "/wetools-cmdb/api/ci", produces = "application/json")
@CrossOrigin(origins = "*")
public class CiController {
    @Autowired
    CiService ciService;

    @Autowired
    CmdbService cmdbService;

    @Autowired
    FieldService fieldService;

    @Autowired
    CiDataService ciDataService;

    @Autowired
    PowerJobApi powerJobApi;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    @PostMapping(path = "/create", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response createCi(@RequestBody CiRequest ciRequest) {
        String ciName = ciRequest.getEnName();
        String env = ciRequest.getEnv();
        List<CiField> fieldList = ciRequest.getFieldList();

        if(!ciService.existedCiMetaCollection(env)) {
            ciService.createCiMetaCollection(env);
        }

        // 判断要创建的CI是否已经存在，已经存在就不重复创建
        if(ciService.existedCi(ciName, env)) {
            return new Response(WetoolsExceptionCode.SUCCESS, "env " + ciRequest.getEnv() + ", " + ciRequest.getEnName() + " ci is existed.", null);
        }

        // 创建CI
        Ci ciRst = ciService.insertOneCi(ciRequest);

        // 没有传入字段信息，默认获取cmdb所有的字段
        if((ciRequest.getFieldList() == null || ciRequest.getFieldList().size() == 0) && ciRequest.getIsCmdb()) {
            List<CiField> ciFieldList = null;
            try {
                ciFieldList = cmdbService.getCmdbCiAllField(ciName, env);
            } catch (Exception e) {
                ciService.deleteCiPhysics(ciRequest.getEnName(), ciRequest.getEnv());
                log.error("get cmdb ci all field failed. error msg: {}", e.getMessage());
                return new Response(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "get cmdb ci all field failed", e.getMessage());
            }

            if(ciFieldList == null || ciFieldList.size() == 0) {
                log.warn("env: [{}], type: [{}] ci is not have data in cmdb.", ciRequest.getEnv(), ciRequest.getEnName());
                return new Response(WetoolsExceptionCode.CMDB_CI_DATA_IS_NULL, "env " + ciRequest.getEnv() + ", " + ciRequest.getEnName() + " ci is not have data in cmdb.", null);
            }
            fieldList = ciFieldList;
        }


        if(!fieldService.existedFieldMetaCollection(env)) {
            fieldService.createFieldCollection(env);
        }

        // 分离CMDB字段和非CMDB字段
        boolean haveCmdbField = false;
        List<String> cmdbCiFieldList = new ArrayList<>();
        List<CiField> noCmdbCiFieldList = new ArrayList<>();
        for(CiField ciField : fieldList) {
            String fieldName = ciField.getEnName();
            if(ciField.getIsCmdb()) {
                haveCmdbField = true;
                cmdbCiFieldList.add(fieldName);
            } else {
                noCmdbCiFieldList.add(ciField);
            }
        }

        fieldList = noCmdbCiFieldList;

        if(haveCmdbField) {
            List<CiField> rst = cmdbService.getCmdbCiField(ciName, cmdbCiFieldList, env);
            fieldList.addAll(rst);
        }

        if (ciRequest.getIsCmdb()) {    // 如果用户自定义了CMDB字段，用户一般不会自定义如下字段，需要加上，因为这几个字段是需要从cmdb同步过来并对之后的同步造成影响的
            fieldList.addAll(fieldService.defaultCmdbCiFields());
        }

        fieldList.addAll(fieldService.defaultNoCmdbCiFields());

        if(fieldList.size() != 0) {
            List<CiField> ciFieldListRst = fieldService.insertAllField(ciName, env, fieldList);
            ciRst.setFieldList(ciFieldListRst);
            ciRequest.setFieldList(ciFieldListRst);
        }

        if(!ciDataService.existedCiDataCollection(ciName, env)) {
            ciDataService.createCiDataCollection(ciName, env);
            // TODO: 对guid设置唯一键索引
        }

        if(ciRequest.getIsCmdb()) {
            // 异步任务，分批全量同步CMDB数据
            if(ciRequest.getSynCmdbCycle() <= 0) {
                ciRequest.setSynCmdbCycle(60000);
            }
            cmdbService.syncManyColumnCmdbDataByFilterAsyncAndRegisterCron(ciRequest);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", ciRst);
    }

    @PostMapping(path = "/create_field", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response createCiField(@RequestBody CiFieldCreateRequest ciFieldCreateRequest) {
        String ciName = ciFieldCreateRequest.getCiName();
        String env = ciFieldCreateRequest.getEnv();
        List<CiField> fieldList = ciFieldCreateRequest.getCiFields();
        if(!ciService.existedCi(ciName, env)) {
            return new Response(WetoolsExceptionCode.FAILED, "ci is not existed", null);
        }

        if(fieldList == null || fieldList.size() == 0) {
            return new Response(WetoolsExceptionCode.SUCCESS, "ci field list is null or not new field", null);
        }

        // 判断字段是否存在已存在的就不重复添加了，同时判断新增的字段是否有CMDB的字段，CMDB的字段需要去CMDB拉取字段信息进行填充
        boolean haveCmdbField = false;
        List<String> fieldNameList = fieldService.findCiAllFieldName(ciName, env);
        List<CiField> ciFieldList = new ArrayList<>(fieldList);

        List<String> cmdbCiFieldList = new ArrayList<>();
        for(CiField ciField : ciFieldList) {
            String fieldName = ciField.getEnName();
            if(fieldNameList.contains(fieldName)) {
                fieldList.remove(ciField);
            } else {
                if(ciField.getIsCmdb()) {
                    haveCmdbField = true;
                    cmdbCiFieldList.add(fieldName);
                    fieldList.remove(ciField);
                }
            }
        }

        if(fieldList.isEmpty() && cmdbCiFieldList.isEmpty()) {
            return new Response(WetoolsExceptionCode.FAILED, "ci field list is existed", null);
        }

        if(haveCmdbField) {
            List<CiField> rst = cmdbService.getCmdbCiField(ciName, cmdbCiFieldList, env);
            fieldList.addAll(rst);
        }

        if(fieldList.size() != 0) {
            List<CiField> ciFieldListRst = fieldService.insertAllField(ciName, env, fieldList);
            ciFieldCreateRequest.setCiFields(ciFieldListRst);

//            Map<String, Object> map = new HashMap<>(ciFieldListRst.size());
//            for(CiField ciField : ciFieldListRst) {
//                map.put(ciField.getEnName(), null);
//            }
//            // 数据集合中所有文档都需要加上新加的字段
//            ciDataService.updateAll(ci.getEnName(), ci.getEnv(), map);
        }

        if (haveCmdbField) {
            // TODO: 这里可以不同步，等定时同步，或者给个手动同步的按钮
            // 从CMDB同步该字段的数据，并更新到数据库中（全量同步一次）
            cmdbService.syncManyColumnCmdbDataAsync(ciFieldCreateRequest);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", ciFieldCreateRequest);
    }

    // TODO: 只是删除了CI和Field元数据(逻辑删除)，真正的数据并未删除
    @PostMapping(path = "/delete", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response deleteCi(@RequestBody CiDeleteRequest ciDeleteRequest) {
        String ciName = ciDeleteRequest.getCiName();
        String env = ciDeleteRequest.getEnv();
        Long cronJobId = ciService.getCiSyncCmdbCronId(ciName, env);
        boolean rst = ciService.deleteCi(ciName, env);

        // 禁用定时任务
        boolean success = powerJobApi.disableCronJob(cronJobId);
        if(!success) {
            log.error("disable cron job {} failed, ci:{}, env:{}", cronJobId, ciName, env);
        }

        if (rst) {
            return new Response(WetoolsExceptionCode.SUCCESS, "success", ciName);
        } else {
            return new Response(WetoolsExceptionCode.FAILED, "failed", ciName);

        }
    }

    // TODO: 仅是删除了字段元数据(逻辑删除)，真正数据中的该字段并未删除
    @PostMapping(path = "/delete_field", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response deleteCiField(@RequestBody CiFieldDeleteRequest ciFieldDeleteRequest) {
        int success = fieldService.deleteField(ciFieldDeleteRequest.getCiName(), ciFieldDeleteRequest.getEnv(), ciFieldDeleteRequest.getCiFields());
        return new Response(WetoolsExceptionCode.SUCCESS, "success", success);
    }

    @PostMapping(path = "/delete_data", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response deleteCiData(@RequestBody CiDataDeleteRequest ciDataDeleteRequest) {
        String ciName = ciDataDeleteRequest.getCiName();
        String env = ciDataDeleteRequest.getEnv();
        int success = 0;
        // TODO: delete data
        // 只能删除非CMDB数据， 是否CMDB数据通过is_cmdb字段来判断
        return new Response(WetoolsExceptionCode.SUCCESS, "success", success);
    }

    @PostMapping(path = "/add_data", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response addCiData(@RequestBody CiDataInsertRequest ciDataInsertRequest) {
        int success = ciDataService.insertCiData(ciDataInsertRequest.getCiName(), ciDataInsertRequest.getEnv(),ciDataInsertRequest.getData());
        return new Response(WetoolsExceptionCode.SUCCESS, "success", success);
    }

    @PostMapping(path = "/update_data", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response updateCiDataByFilter(@RequestBody CiDataUpdateRequest ciDataUpdateRequest) {
        if(ciDataUpdateRequest.getCiDataUpdateList() == null || ciDataUpdateRequest.getCiDataUpdateList().size() == 0) {
            return new Response(WetoolsExceptionCode.FAILED, "need to update data is null", null);
        }

        Ci ci = new Ci();
        ci.setEnv(ciDataUpdateRequest.getEnv());
        ci.setEnName(ciDataUpdateRequest.getCiName());
        long success = 0;
        try {
            success = ciDataService.updateCiData(ci, ciDataUpdateRequest.getCiDataUpdateList());
        } catch (Exception e) {
            log.error("update ci data by filter failed! ", e);
            return new Response(WetoolsExceptionCode.FAILED, "update ci data by filter failed! " + e.getMessage(), null);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", success);
    }

    @PostMapping(path = "/get", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response getCiMetadata(@RequestBody Ci ci) {
         Ci ciMetadata = ciService.findCi(ci.getEnName(), ci.getEnv());

        return new Response(WetoolsExceptionCode.SUCCESS, "success", ciMetadata);
    }

    // TODO: 连通CI字段的属性一起返回，前端根据字段属性决定是否展示
    @PostMapping(path = "/get_data", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response getCiData(@RequestBody CiDataRequest ciDataRequest) {
        String ciName = ciDataRequest.getCiName();
        String env = ciDataRequest.getEnv();

        if(ciName == null || ciName.length() <= 0) {
            return new Response(WetoolsExceptionCode.FAILED, "ci enName must be not null", null);
        }
        if(env == null || env.length() <= 0) {
            return new Response(WetoolsExceptionCode.FAILED, "ci env must be not null", null);
        }

        List<String> resultColumn = ciDataRequest.getResultColumn();
        Map<String, Object> filter = ciDataRequest.getFilter();

        List<Document> fieldList = fieldService.findCiFiled(ciName, env, resultColumn);

        List<Map<String, Object>> rst;
        try {
            // TODO: 分页
            rst = ciDataService.getData(ciName, env, filter, resultColumn);
        } catch (Exception e) {
            log.warn("get data failed, ", e);
            return new Response(WetoolsExceptionCode.FAILED, "get data failed, " + e.getMessage(), null);
        }
        return new Response(WetoolsExceptionCode.SUCCESS, "success", new CiDataResponse(fieldList, rst));
    }
}
