package com.webank.wetoolscmdb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.model.dto.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void createCi() throws Exception {
        Map<String , Object> filter = new HashMap<>();
        filter.put("system_domain", "工具域");
        List<CiField> fieldList = new ArrayList<>();
        CiField ciField1 = new CiField("1", "dcn", "dcn", true, true, null, 0, null, null, null, null);
        CiField ciField2 = new CiField("2", "内网IP", "host_lanip", true, true, null, 0, null, null, null, null);
        fieldList.add(ciField1);
        fieldList.add(ciField2);
        CiField ciField3 = new CiField("1", "模块", "module", false, true, null, 0, null, null, null, null);
        fieldList.add(ciField3);

        CiRequest ci = new CiRequest("主机", "wb_host", true, "uat", filter, 60000,  fieldList);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ci);
        // 执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/wetools-cmdb/api/ci/create")
                        .content(json.getBytes()) //传json参数
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        Thread.sleep(200000000);
    }

    @Test
    public void deleteCi() throws Exception {
        CiDeleteRequest ci = new CiDeleteRequest("wb_host", "uat");
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ci);
        // 执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/wetools-cmdb/api/ci/delete")
                        .content(json.getBytes()) //传json参数
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        Thread.sleep(200000000);
    }

    @Test
    public void createField() throws Exception {
        List<CiField> ciFieldList = new ArrayList<>();
        CiField ciField = new CiField();
        ciField.setIsCmdb(true);
        ciField.setEnName("all_ip");
        ciFieldList.add(ciField);

        CiField ciField1 = new CiField();
        ciField1.setIsCmdb(false);
        ciField1.setEnName("test");
        ciFieldList.add(ciField1);

        Map<String , Object> filter = new HashMap<>();
        filter.put("system_domain", "工具域");

        CiFieldCreateRequest ci = new CiFieldCreateRequest("wb_host", "uat", ciFieldList, filter);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ci);
        // 执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/wetools-cmdb/api/ci/create_field")
                        .content(json.getBytes()) //传json参数
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        Thread.sleep(200000000);
    }

    @Test
    public void updateCiData() throws Exception {
        CiDataUpdateRequest ciDataUpdateRequest = new CiDataUpdateRequest();
        ciDataUpdateRequest.setCiName("wb_host");
        ciDataUpdateRequest.setEnv("uat");

        List<CiDataUpdate> dataList = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put("module", "test");
        Map<String, Object> filter = new HashMap<>();
        filter.put("_id", "647080a2fd0ea47480802b64");
        CiDataUpdate ciDataUpdate = new CiDataUpdate(data, filter);
        dataList.add(ciDataUpdate);

        Map<String, Object> data1 = new HashMap<>();
        data1.put("dcn", "test");
        data1.put("module", "test1");
        Map<String, Object> filter1 = new HashMap<>();
        filter1.put("_id", "647080a2fd0ea47480802b65");
        CiDataUpdate ciDataUpdate1 = new CiDataUpdate(data1, filter1);
        dataList.add(ciDataUpdate1);

        ciDataUpdateRequest.setCiDataUpdateList(dataList);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ciDataUpdateRequest);
        // 执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/wetools-cmdb/api/ci/update_data")
                        .content(json.getBytes()) //传json参数
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        Thread.sleep(200000000);
    }

    @Test
    public void addCiData() throws Exception {
        Map<String, Object> data1 = new HashMap<>();
        data1.put("dcn", "XXX");
        data1.put("host_lanip", "x.x.x.x");
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(data1);
        CiDataInsertRequest ciDataInsertRequest = new CiDataInsertRequest("wb_host", "uat", data);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ciDataInsertRequest);
        // 执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/wetools-cmdb/api/ci/add_data")
                        .content(json.getBytes()) //传json参数
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        Thread.sleep(200000000);
    }

    @Test
    public void deleteCiField() throws Exception {
        List<String> ciFieldList = new ArrayList<>();
        ciFieldList.add("test");
        CiFieldDeleteRequest ci = new CiFieldDeleteRequest("wb_host", "uat", ciFieldList);
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ci);
        // 执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/wetools-cmdb/api/ci/delete_field")
                        .content(json.getBytes()) //传json参数
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

        Thread.sleep(200000000);
    }
}