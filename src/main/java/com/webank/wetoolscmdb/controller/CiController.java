package com.webank.wetoolscmdb.controller;

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

import java.util.List;

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
        ciService.createCi(ci);

        if(ci.getFieldList() == null && ci.getIsCmdb()) {
            List<CiField> ciFieldList =  cmdbService.getCmdbCiAllField(ci);
            if(ciFieldList.size() == 0) {
                log.warn("env " + ci.getEnv() + ", " + ci.getEnName() + " ci is not have data in cmdb.");
                return new Response(WetoolsExceptionCode.CMDB_CI_DATA_IS_NULL, "env " + ci.getEnv() + ", " + ci.getEnName() + " ci is not have data in cmdb.", null);
            }
            ci.setFieldList(ciFieldList);
        }

        if(!fieldService.existedFieldMetaCollection(ci)) {
            fieldService.createFieldCollection(ci);
        }

        fieldService.createField(ci);

        if(!ciDataService.existedCiDataCollection(ci)) {
            ciDataService.createCiDataCollection(ci);
        }

        if(ci.getIsCmdb()) {
            // 异步任务，分批全量同步CMDB数据
            cmdbService.syncManyColumnCmdbAllDataAsync(ci);
        }


        return new Response(WetoolsExceptionCode.SUCCESS, "success", ci);
    }
}
