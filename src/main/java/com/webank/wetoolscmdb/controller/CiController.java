package com.webank.wetoolscmdb.controller;

import com.webank.wetoolscmdb.constant.consist.CiQueryConsist;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.model.dto.Ci;
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
import java.util.Date;
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
    public Response createCi(@RequestBody Ci ci) {
        // metadata ci collection已经存在了，不需要再创建了
        if(!ciService.existedCiMetaCollection(ci)) {
            ciService.createCiMetaCollection(ci);
        }

        // 判断要创建的CI是否已经存在，已经存在就不重复创建
        if(ciService.existedCi(ci)) {
            return new Response(WetoolsExceptionCode.SUCCESS, "env " + ci.getEnv() + ", " + ci.getEnName() + " ci is existed.", null);
        }

        // 创建CI
        Ci ciRst = ciService.insertOneCi(ci);

        if((ci.getFieldList() == null || ci.getFieldList().size() == 0) && ci.getIsCmdb()) {
            List<CiField> ciFieldList =  cmdbService.getCmdbCiAllField(ci);
            if(ciFieldList.size() == 0) {
                log.warn("env " + ci.getEnv() + ", " + ci.getEnName() + " ci is not have data in cmdb.");
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
            cmdbService.syncManyColumnCmdbAllDataAsyncAndRegisterCron(ci);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", ciRst);
    }

    @PostMapping(path = "/create_field", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Response createCiField(@RequestBody Ci ci) {
        if(ci.getFieldList() == null || ci.getFieldList().size() == 0) {
            return new Response(WetoolsExceptionCode.SUCCESS, "success", "ci field list is null or not new field");
        }

        // 判断字段是否存在，同时判断新增的字段是否有CMDB的字段
        boolean haveCmdbField = false;
        List<String> fieldNameList = fieldService.findCiAllFieldName(ci.getEnName(), ci.getEnv());
        for(CiField ciField : ci.getFieldList()) {
            if(fieldNameList.contains(ciField.getEnName())) {
                ci.getFieldList().remove(ciField);
            } else {
                if(ciField.getIsCmdb()) {
                    haveCmdbField = true;
                }
            }
        }

        if(ci.getFieldList() != null && ci.getFieldList().size() != 0) {
            List<CiField> ciFieldListRst = fieldService.insertAllField(ci);
            ci.setFieldList(ciFieldListRst);

            Map<String, Object> map = new HashMap<>(ciFieldListRst.size());
            for(CiField ciField : ciFieldListRst) {
                map.put(ciField.getEnName(), null);
            }
            // 数据集合中所有文档都需要加上新加的字段
            ciDataService.updateAll(ci.getEnName(), ci.getEnv(), map);
        }

        if (haveCmdbField) {
            // TODO: 从CMDB同步该字段的数据，并更新到数据库中（全量同步一次）
            cmdbService.syncManyColumnCmdbDataAsync(ci);
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", ci);
    }

    @PostMapping(path = "/delete", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
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
    public Response deleteCiField(@RequestBody Ci ci) {
        int success = 0;
        for(CiField ciField : ci.getFieldList()) {
            success += fieldService.deleteField(ci.getEnName(), ci.getEnv(), ciField.getEnName()) ? 1 : 0;
        }

        return new Response(WetoolsExceptionCode.SUCCESS, "success", success);
    }
}
