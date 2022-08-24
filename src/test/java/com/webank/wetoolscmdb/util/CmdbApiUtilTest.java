package com.webank.wetoolscmdb.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseData;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseDataHeader;
import com.webank.wetoolscmdb.utils.cmdb.CmdbApiUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CmdbApiUtilTest {
    @Autowired
    CmdbApiUtil cmdbApiUtil;

    @Test
    public void testGetCiFiledAttributes() {
        String type = "wb_host";
        Map<String, CmdbResponseDataHeader> ciFiledAttributes = cmdbApiUtil.getCiFiledAttributes(type);
        System.out.println(ciFiledAttributes);
    }

    @Test
    public void testGetTemplateFiledAttributes() {
        String type = "subsystem_app_instance";
        Map<String, CmdbResponseDataHeader> templateFiledAttributes = cmdbApiUtil.getTemplateFiledAttributes(type);
        System.out.println(templateFiledAttributes);
    }

    @Test
    public void testGetCiData() {
        String type = "wb_host";
        Map<String, Object> filter = new HashMap<>();
        filter.put("system_domain", "工具域");
        List<String> resultColumn = new ArrayList<>();
        resultColumn.add("host_lanip");
        CmdbResponseData ciData = cmdbApiUtil.getCiData(type, filter, resultColumn);
        System.out.println(ciData.getContent());
    }

    @Test
    public void testGetTemplateData() {
        String type = "hostInfo";
        CmdbResponseData templateData = cmdbApiUtil.getTemplateData(type);
        System.out.println(templateData.getContent().size());
    }

    @Test
    public void testParseCmdbResponseData() throws ParseException {
        String type = "wb_host";
        CmdbResponseData templateData = cmdbApiUtil.getCiData(type);
        System.out.println(cmdbApiUtil.parseCmdbResponseData(templateData));
    }
}
