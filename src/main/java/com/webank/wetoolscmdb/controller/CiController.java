package com.webank.wetoolscmdb.controller;

import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiData;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.dto.Response;
import com.webank.wetoolscmdb.service.intf.CiDataService;
import com.webank.wetoolscmdb.service.intf.CiService;
import com.webank.wetoolscmdb.service.intf.CmdbService;
import com.webank.wetoolscmdb.service.intf.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    @PostMapping(path = "/create", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response createCi(@RequestBody Ci ci) {
        // 判断要创建的CI是否已经存在，已经存在就不重复创建
        if(!ciService.existedCiMetaCollection(ci)) {
            ciService.createCiMetaCollection(ci);
        }

        if(ciService.existedCi(ci)) {
            return new Response(WetoolsExceptionCode.SUCCESS, "env " + ci.getEnv() + ", " + ci.getEnName() + " ci is existed.", null);
        }

        // 创建CI
        Ci ciRst = ciService.insertOneCi(ci);

        if((ci.getFieldList() == null || ci.getFieldList().size() == 0) && ci.getIsCmdb()) {
            List<CiField> ciFieldList = null;
            try {
                ciFieldList = cmdbService.getCmdbCiAllField(ci);
            } catch (Exception e) {
                ciService.deleteCiPhysics(ci.getEnName(), ci.getEnv());
                log.error("get cmdb ci all field failed. error msg: {}", e.getMessage());
                return new Response(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "get cmdb ci all field failed", e.getMessage());
            }

            if(ciFieldList == null || ciFieldList.size() == 0) {
                log.warn("env: [{}], type: [{}] ci is not have data in cmdb.", ci.getEnv(), ci.getEnName());
                return new Response(WetoolsExceptionCode.CMDB_CI_DATA_IS_NULL, "env " + ci.getEnv() + ", " + ci.getEnName() + " ci is not have data in cmdb.", null);
            }
            ci.setFieldList(ciFieldList);
        } else if (ci.getIsCmdb()) {    // 如果用户自定义了CMDB字段，用户一般不会自定义如下字段，需要加上，因为这几个字段是需要从cmdb同步过来并对之后的同步造成影响的
            ci.getFieldList().addAll(fieldService.defaultCmdbCiFields());
        }

        if(!fieldService.existedFieldMetaCollection(ci)) {
            fieldService.createFieldCollection(ci);
        }

        if(ci.getFieldList() != null && ci.getFieldList().size() != 0) {
            List<CiField> ciFieldListRst = fieldService.insertAllField(ci);
            ciRst.setFieldList(ciFieldListRst);
        }

        if(!ciDataService.existedCiDataCollection(ci)) {
            ciDataService.createCiDataCollection(ci);
        }

        if(ci.getIsCmdb()) {
            // 异步任务，分批全量同步CMDB数据
            if(ci.getSynCmdbCycle() <= 0) {
                ci.setSynCmdbCycle(60000);
            }
            cmdbService.syncManyColumnCmdbDataAsyncAndRegisterCron(ci);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", ciRst);
    }

    @PostMapping(path = "/create_field", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response createCiField(@RequestBody Ci ci) {
        if(!ciService.existedCi(ci)) {
            return new Response(WetoolsExceptionCode.SUCCESS, "success", "ci is not existed");
        }

        if(ci.getFieldList() == null || ci.getFieldList().size() == 0) {
            return new Response(WetoolsExceptionCode.SUCCESS, "success", "ci field list is null or not new field");
        }

        // 判断字段是否存在已存在的就不重复添加了，同时判断新增的字段是否有CMDB的字段，CMDB的字段需要去CMDB拉取字段信息进行填充
        boolean haveCmdbField = false;
        List<String> fieldNameList = fieldService.findCiAllFieldName(ci.getEnName(), ci.getEnv());
        List<CiField> ciFieldList = new ArrayList<>(ci.getFieldList());

        List<String> cmdbCiFieldList = new ArrayList<>();
        for(CiField ciField : ciFieldList) {
            if(fieldNameList.contains(ciField.getEnName())) {
                ci.getFieldList().remove(ciField);
            } else {
                if(ciField.getIsCmdb()) {
                    haveCmdbField = true;
                    cmdbCiFieldList.add(ciField.getEnName());
                    ci.getFieldList().remove(ciField);
                }
            }
        }

        if(ci.getFieldList().isEmpty() && cmdbCiFieldList.isEmpty()) {
            return new Response(WetoolsExceptionCode.SUCCESS, "success", "ci field list is existed");
        }

        if(haveCmdbField) {
            List<CiField> rst = cmdbService.getCmdbCiField(ci.getEnName(), cmdbCiFieldList, ci.getEnv());
            ci.getFieldList().addAll(rst);
        }

        if(ci.getFieldList() != null && ci.getFieldList().size() != 0) {
            List<CiField> ciFieldListRst = fieldService.insertAllField(ci);
            ci.setFieldList(ciFieldListRst);

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
            cmdbService.syncManyColumnCmdbDataAsync(ci);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", ci);
    }

    @PostMapping(path = "/delete", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response deleteCi(@RequestBody Ci ci) {
        boolean rst = ciService.deleteCi(ci.getEnName(), ci.getEnv());

        if (rst) {
            return new Response(WetoolsExceptionCode.SUCCESS, "success", ci);
        } else {
            return new Response(WetoolsExceptionCode.FAILED, "failed", ci);

        }
    }

    @PostMapping(path = "/delete_field", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response deleteCiField(@RequestBody Ci ci) {
        int success = 0;
        for(CiField ciField : ci.getFieldList()) {
            success += fieldService.deleteField(ci.getEnName(), ci.getEnv(), ciField.getEnName()) ? 1 : 0;
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", success);
    }

    // 手动新增的数据，GUID都为空，调用该接口新增数据时，需要将CI所有字段都填充好，没有值的为null
    @PostMapping(path = "/add_data", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response addCiData(@RequestBody CiData ciData) {
        Ci ci = new Ci();
        ci.setEnv(ciData.getEnv());
        ci.setEnName(ciData.getEnName());
        int success = ciDataService.insertCiData(ci, ciData.getData());

        return new Response(WetoolsExceptionCode.SUCCESS, "success", success);
    }

    // 目前只能修改非CMDB的数据，更新时只需要填充需要更新的字段即可，但是需要带上_id，会根据ID去寻找需要更新的记录是哪条
    @PostMapping(path = "/update_data", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response updateCiData(@RequestBody CiData ciData) {
        if(ciData.getData() == null || ciData.getData().size() == 0) {
            return new Response(WetoolsExceptionCode.SUCCESS, "success", "data is not existed");
        }

        Ci ci = new Ci();
        ci.setEnv(ciData.getEnv());
        ci.setEnName(ciData.getEnName());
        int success = ciDataService.updateCiData(ci, ciData.getData());

        if(success == -1) {
            return new Response(WetoolsExceptionCode.FAILED, "not have id", null);
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

    @PostMapping(path = "/get_data", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Response getCiData(@RequestBody Ci ci) {
        if(ci.getEnName() == null || ci.getEnName().length() <= 0) {
            return new Response(WetoolsExceptionCode.FAILED, "ci enName must be not null", null);
        }
        if(ci.getEnv() == null || ci.getEnv().length() <= 0) {
            return new Response(WetoolsExceptionCode.FAILED, "ci env must be not null", null);
        }
        List<String> resultColumn = new ArrayList<>(ci.getFieldList().size());
        for(CiField field : ci.getFieldList()) {
            resultColumn.add(field.getEnName());
        }

        List<Map<String, Object>> rst;
        try {
            // TODO: 分页
            rst = ciDataService.getData(ci.getEnName(), ci.getEnv(), ci.getFilter(), resultColumn);
        } catch (Exception e) {
            log.warn("get data failed, ", e);
            return new Response(WetoolsExceptionCode.FAILED, "get data failed, " + e.getMessage(), null);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", rst);
    }
}
