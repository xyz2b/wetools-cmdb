package com.webank.wetoolscmdb.controller;

import com.webank.wetoolscmdb.model.entity.mongo.Ci;
import com.webank.wetoolscmdb.mapper.intf.mongo.CiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@Controller
@RequestMapping(path = "/api/test")
@CrossOrigin(origins = "*")
public class TestController {
    @Autowired
    private CiRepository ciRepository;

    @GetMapping(path = "/mongodb")
    @ResponseStatus(HttpStatus.OK)
    public void appInstance() {
        Ci ci = new Ci();
        ci.setCnName("应用域").setEnName("wb_Applicationdomain");
        ci.setCmdb(true);
        ci.setDelete(false);
        ci.setCreatedDate(new Date());
        ci.setUpdatedDate(new Date());
        ci.setCIDataLastUpdateDate(new Date());
        System.out.println("save success");

        ciRepository.saveCi(ci);
    }
}
