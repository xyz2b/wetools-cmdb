package com.webank.wetoolscmdb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiData;
import com.webank.wetoolscmdb.model.dto.CiField;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
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
        Ci ci = new Ci("0", "主机", "wb_host", true, "uat", 30000,  null, null, null, null, null, null, null);
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
    public void createField() throws Exception {
        List<CiField> ciFieldList = new ArrayList<>();
        CiField ciField = new CiField();
        ciField.setIsCmdb(false);
        ciField.setEnName("test");
        ciFieldList.add(ciField);

        Ci ci = new Ci("0", "主机", "wb_host", true, "uat", 30000,  ciFieldList, null, null, null, null, null, null);
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
        CiData ciData = new CiData();
        ciData.setEnName("wb_host");
        ciData.setEnv("uat");

        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> kv = new HashMap<>();
        kv.put("_id", "63213cc3d7f95f74c8a0259c");
        kv.put("test", "ddsadasd");
        data.add(kv);

        ciData.setData(data);

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ciData);
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
    public void deleteCiField() throws Exception {
        List<CiField> ciFieldList = new ArrayList<>();
        CiField ciField = new CiField();
        ciField.setEnName("test");
        ciFieldList.add(ciField);

        Ci ci = new Ci("0", "主机", "wb_host", true, "uat", 30000,  ciFieldList, null, null, null, null, null, null);
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