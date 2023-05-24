package com.webank.wetoolscmdb.utils.cmdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.config.CmdbApiProperties;
import com.webank.wetoolscmdb.constant.consist.*;
import com.webank.wetoolscmdb.model.dto.cmdb.*;
import com.webank.wetoolscmdb.utils.exception.WetoolsCmdbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CmdbApiUtil {
    @Autowired
    private CmdbApiProperties props;
    @Autowired
    private RestTemplate rest;

    private final String CMDB_API_URL = "/cmdb/api/";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    private static final ObjectMapper objectMapper = new ObjectMapper();


    public List<Map<String, Object>> parseCmdbResponseData(CmdbResponseData cmdbResponseData) {
        Map<String, CmdbResponseDataHeader> fieldAttributes = new HashMap<>(cmdbResponseData.getHeader().size());
        List<CmdbResponseDataHeader> cmdbResponseDataHeaders = cmdbResponseData.getHeader();
        for(CmdbResponseDataHeader cmdbResponseDataHeader : cmdbResponseDataHeaders) {
            fieldAttributes.put(cmdbResponseDataHeader.getEnName(), cmdbResponseDataHeader);
        }

        List<Map<String, Object>> contents = new ArrayList<>(cmdbResponseData.getContent().size());
        for(Map<String, Object> fieldData : cmdbResponseData.getContent()) {
            Map<String, Object> content = new HashMap<>(fieldData.size());
            for(Map.Entry<String, Object> field : fieldData.entrySet()) {
                if(fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.REF)) {
                    List<Map<String, String>> value = (List<Map<String, String>>) field.getValue();
                    if(value != null) {
                        content.put(field.getKey(), value.get(0).get("v"));
                    } else {
                        content.put(field.getKey(), null);
                    }
                } else if (fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.MULTI_REF)) {
                    List<Map<String, String>> value = (List<Map<String, String>>) field.getValue();
                    if(value != null) {
                        List<String> list = new ArrayList<>(value.size());
                        for(Map<String, String> d : value) {
                            list.add(d.get("v"));
                        }
                        content.put(field.getKey(), list);
                    } else {
                        content.put(field.getKey(), null);
                    }
                } else if (fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.SELECT)) {
                    String value = (String) field.getValue();
                    content.put(field.getKey(), value);
                } else if (fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.TEXT)) {
                    String value = (String) field.getValue();
                    content.put(field.getKey(), value);
                } else if (fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.TEXTAREA)) {
                    String value = (String) field.getValue();
                    content.put(field.getKey(), value);
                } else if (fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.NUMBER)) {
                    Integer value = (Integer) field.getValue();
                    content.put(field.getKey(), value);
                } else if (fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.HIDDEN)) {
                    String value = (String) field.getValue();
                    content.put(field.getKey(), value);
                } else if (fieldAttributes.get(field.getKey()).getDataType().equals(CmdbQueryResponseDataType.DATE)) {
                    String value = (String) field.getValue();
                    if(value == null) {
                        content.put(field.getKey(), null);
                    } else {
                        try {
                            if (value.length() == CmdbApiConsist.DATE_FORMAT_DAY.length()) {
                                content.put(field.getKey(), SIMPLE_DATE_FORMAT_DAY.parse(value));
                            } else if (value.length() == CmdbApiConsist.DATE_FORMAT_SECOND.length()) {
                                content.put(field.getKey(), SIMPLE_DATE_FORMAT_SECOND.parse(value));
                            } else if (value.length() == CmdbApiConsist.DATE_FORMAT_MILLISECOND.length()) {
                                content.put(field.getKey(), SIMPLE_DATE_FORMAT_MILLISECOND.parse(value));
                            } else {
                                log.warn("parse cmdb response data field date type failed, code: [{}], field_name: [{}], type: [{}], msg: [unknown date format], value: [{}]", WetoolsExceptionCode.UNKNOWN_CMDB_TYPE_ERROR, field.getKey(), fieldAttributes.get(field.getKey()).getDataType(), value);
                            }
                        } catch (ParseException e) {
                            log.warn("parse cmdb response data field date type failed, code: [{}], field_name: [{}], type: [{}], error: [{}]", WetoolsExceptionCode.UNKNOWN_CMDB_TYPE_ERROR, field.getKey(), fieldAttributes.get(field.getKey()).getDataType(), e.getMessage());
                            e.printStackTrace();
                        }
                    }
                } else {
                    log.warn("unknown cmdb response data field type, code: [{}], field_name: [{}], type: [{}]", WetoolsExceptionCode.UNKNOWN_CMDB_TYPE_ERROR, field.getKey(), fieldAttributes.get(field.getKey()).getDataType());
                }
            }
            contents.add(content);
        }

        return contents;
    }

    // 获取某个CI的总数据量
    public int getCiDataCount(String type, String env) {
        CmdbResponse response = standardQueryCmdb(type, 0, 1, true, new HashMap<>(0), new ArrayList<>(0), env);
        return Integer.parseInt(response.getHeaders().getTotalRows());
    }

    // 获取某个CI指定过滤条件的数据量
    public int getCiDataCount(String type, Map<String, Object> filter, String env) {
        CmdbResponse response = standardQueryCmdb(type, 0, 1, true, filter, new ArrayList<>(0), env);
        return Integer.parseInt(response.getHeaders().getTotalRows());
    }

    // 获取某个CI所有字段的属性信息
    public Map<String, CmdbResponseDataHeader> getCiFiledAttributes(String type, String env) {
        return getCiFiledAttributes(type, new ArrayList<>(0), env);
    }

    // 获取某个CI指定字段的属性信息
    // guid、created_by、created_date、updated_date是默认属性，指定不指定这四个字段都会返回这四个字段的属性信息
    public Map<String, CmdbResponseDataHeader> getCiFiledAttributes(String type, List<String> resultColumn, String env) {
        CmdbResponse response = standardQueryCmdb(type, 0, 0, true, new HashMap<>(0), resultColumn, env);
        Map<String, CmdbResponseDataHeader> ciFiledAttributes = new HashMap<>(response.getData().getHeader().size());
        for(CmdbResponseDataHeader header : response.getData().getHeader()) {
            ciFiledAttributes.put(header.getEnName(), header);
        }
        return ciFiledAttributes;
    }

    // 获取综合查询模板所有字段的属性信息
    public Map<String, CmdbResponseDataHeader> getTemplateFiledAttributes(String type, String env) {
        return getTemplateFiledAttributes(type, new ArrayList<>(0), env);
    }

    // 获取综合查询模板指定字段的属性信息
    // 目前CMDB并未对综合查询模板的指定字段进行过滤，不管是否指定字段，都会返回所有字段的属性信息
    public Map<String, CmdbResponseDataHeader> getTemplateFiledAttributes(String type, List<String> resultColumn, String env) {
        CmdbResponse response = templateQueryCmdb(type, 0, 0, true, new HashMap<>(0), resultColumn, env);
        Map<String, CmdbResponseDataHeader> ciFiledAttributes = new HashMap<>(response.getData().getHeader().size());
        for(CmdbResponseDataHeader header : response.getData().getHeader()) {
            ciFiledAttributes.put(header.getEnName(), header);
        }
        return ciFiledAttributes;
    }

    // 获取某个CI的所有数据
    public CmdbResponseData getCiData(String type, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse firstResponse = standardQueryCmdb(type, 0, pageSize,true, new HashMap<>(0), new ArrayList<>(0), env);

        getCiAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    // 获取某个CI指定 过滤条件、返回字段 的所有数据
    public CmdbResponseData getCiData(String type, Map<String, Object> filter, List<String> resultColumn, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse firstResponse = standardQueryCmdb(type, 0, pageSize,true, filter, resultColumn, env);

        getCiAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    // 获取某个CI指定 过滤条件 的所有数据
    public CmdbResponseData getCiData(String type, Map<String, Object> filter, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse firstResponse = standardQueryCmdb(type, 0, pageSize,true, filter, new ArrayList<>(0), env);

        getCiAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    // 获取某个CI指定 返回字段 的所有数据
    public CmdbResponseData getCiData(String type, List<String> resultColumn, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse firstResponse = standardQueryCmdb(type, 0, pageSize,true, new HashMap<>(0), resultColumn, env);

        getCiAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    // 获取某个CI指定 返回字段 的startIndex开始的page条数据
    public CmdbResponse getCiDataByStartIndex(String type, List<String> resultColumn, int startIndex, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        return standardQueryCmdb(type, startIndex, pageSize,true, new HashMap<>(0), resultColumn, env);
    }

    // 获取某个CI的startIndex开始的page条数据
    public CmdbResponse getCiDataByStartIndex(String type, int startIndex, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        return standardQueryCmdb(type, startIndex, pageSize,true, new HashMap<>(0), new ArrayList<>(0), env);
    }

    // 获取某个CI指定 过滤条件 返回字段 的startIndex开始的page条数据
    public CmdbResponse getCiDataByStartIndex(String type, Map<String, Object> filter, List<String> resultColumn, int startIndex, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        return standardQueryCmdb(type, startIndex, pageSize,true, filter, resultColumn, env);
    }

    // 获取某个CI指定 过滤条件、返回字段、返回数量(startIndex, pageSize) 的数据
    public CmdbResponseData getCiData(String type, int startIndex, int pageSize, Map<String, Object> filter, List<String> resultColumn, String env) {
        CmdbResponse response = standardQueryCmdb(type, startIndex, pageSize, true, filter, resultColumn, env);
        return response.getData();
    }

    private void getCiAllPageData(String type, CmdbResponse firstResponse, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse response = firstResponse;
        while (!isLastPage(response)) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + pageSize, pageSize,true, new HashMap<>(0), new ArrayList<>(0), env);

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }
    }

    // 判断是不是最后一页数据
    public boolean isLastPage(CmdbResponse response) {
        // 本次获取的startIndex加上本次获取的分页大小，就是下一次要获取的startIndex，如果下一次startIndex已经超出了总数据量，就说明获取了全部的数据了
        int nextStartIndex = Integer.parseInt(response.getHeaders().getStartIndex()) + response.getHeaders().getContentRows();
        return nextStartIndex >= Integer.parseInt(response.getHeaders().getTotalRows());
    }

    public int nextIndex(CmdbResponse response) {
        return Integer.parseInt(response.getHeaders().getStartIndex()) + response.getHeaders().getContentRows();
    }

    // 获取某个 Template 的所有数据
    public CmdbResponseData getTemplateData(String type, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse firstResponse = templateQueryCmdb(type, 0, pageSize,true, new HashMap<>(0), new ArrayList<>(0), env);

        getTemplateAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    // 获取某个 Template 指定 过滤条件、返回字段 的所有数据
    public CmdbResponseData getTemplateData(String type, Map<String, Object> filter, List<String> resultColumn, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse firstResponse = templateQueryCmdb(type, 0, pageSize,true, filter, resultColumn, env);

        getTemplateAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    // 获取某个 Template 指定 过滤条件 的所有数据
    public CmdbResponseData getTemplateData(String type, Map<String, Object> filter, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);
        CmdbResponse firstResponse = templateQueryCmdb(type, 0, pageSize,true, filter, new ArrayList<>(0), env);

        getTemplateAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    // 获取某个 Template 指定 返回字段 的所有数据
    public CmdbResponseData getTemplateData(String type, List<String> resultColumn, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);

        CmdbResponse firstResponse = templateQueryCmdb(type, 0, pageSize,true, new HashMap<>(0), resultColumn, env);

        getTemplateAllPageData(type, firstResponse, env);

        return firstResponse.getData();
    }

    private void getTemplateAllPageData(String type, CmdbResponse firstResponse, String env) {
        int pageSize = props.getPageSize().getOrDefault(env, 500);
        CmdbResponse response = firstResponse;
        // 由于CMDB 综合查询接口 实际返回行数有问题，只能下面这样判断
        while (response.getHeaders().getContentRows() != 0) {
            response = templateQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + pageSize, pageSize,true, new HashMap<>(0), new ArrayList<>(0), env);

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }
    }

    // 获取某个Template指定 过滤条件、返回字段、返回数量(startIndex, pageSize) 的数据
    public CmdbResponseData getTemplateData(String type, int startIndex, int pageSize, Map<String, Object> filter, List<String> resultColumn, String env) {
        CmdbResponse response = templateQueryCmdb(type, startIndex, pageSize, true, filter, resultColumn, env);
        return response.getData();
    }

    // CMDB统一查询接口
    private CmdbResponse standardQueryCmdb(String type, int startIndex, int pageSize, boolean isPaging, Map<String, Object> filter, List<String> resultColumn, String env) {
        String cmdbUrl = props.getUrl().get(env);
        if(cmdbUrl == null) {
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, env + " url is null");
        }
        String url = cmdbUrl + CMDB_API_URL + CmdbApiConsist.STANDARD_QUERY + ".json";
        return queryCmdb(url, type, startIndex, pageSize, isPaging, filter, resultColumn, env);
    }

    // CMDB综合查询接口
    private CmdbResponse templateQueryCmdb(String type, int startIndex, int pageSize, boolean isPaging, Map<String, Object> filter, List<String> resultColumn, String env) {
        String cmdbUrl = props.getUrl().get(env);
        if(cmdbUrl == null) {
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, env + " url is null");
        }
        String url = cmdbUrl + CMDB_API_URL + CmdbApiConsist.TEMPLATE_QUERY + ".json";
        return queryCmdb(url, type, startIndex, pageSize, isPaging, filter, resultColumn, env);
    }

    // 查询CMDB数据的基础方法
    private CmdbResponse queryCmdb(String url, String type, int startIndex, int pageSize, boolean isPaging,
                                 Map<String, Object> filter, List<String> resultColumn, String env) {
        String authUser = props.getAuthUser().get(env);
        if(authUser == null) {
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, env + " url is null");
        }
        CmdbRequest request = new CmdbRequest(authUser, type, startIndex, pageSize, CmdbApiConsist.ACTION_SELECT, isPaging, filter, resultColumn);

        String response = rest.postForObject(url, request, String.class);

        Object serResponse;
        try {
            serResponse = serializationResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, e.getMessage());
        }

        checkCmdbResponse(serResponse, request, url);

        return (CmdbResponse) serResponse;
    }

    private static void checkCmdbResponse(Object response, CmdbRequest request, String url) {
        if (response instanceof CmdbResponseError) {
            CmdbResponseError cmdbResponseError = (CmdbResponseError) response;
            log.warn("request cmdb error: " + cmdbResponseError.getMsg());
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: " + cmdbResponseError.getMsg());
        }

        CmdbResponse cmdbResponse = (CmdbResponse) response;
        if (cmdbResponse == null) {
            log.error("request cmdb error: response is null, code: [{}], type: [{}], url: [{}]", WetoolsExceptionCode.REQUEST_CMDB_ERROR, request.getType(), url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response is null, type: " + request.getType());
        }

        log.debug("request cmdb body: [{}]", request.toString());

        if (cmdbResponse.getHeaders() == null) {
            log.error("request cmdb error: response headers is null, code: [{}], type: [{}], url: [{}]", WetoolsExceptionCode.REQUEST_CMDB_ERROR, request.getType(), url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response headers is null, type: " + request.getType());
        }

        if (cmdbResponse.getHeaders().getRetCode() != 0) {
            log.error("request cmdb error: [{}], code: [{}], type: [{}], url: [{}]", cmdbResponse.getHeaders().getErrorInfo(), WetoolsExceptionCode.REQUEST_CMDB_ERROR, request.getType(), url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: " + cmdbResponse.getHeaders().getErrorInfo() + ", type: " + request.getType());
        }

        if (cmdbResponse.getData() == null) {
            log.error("request cmdb error: response data is null, code: [{}], type: [{}], url: [{}]", WetoolsExceptionCode.REQUEST_CMDB_ERROR, request.getType(), url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response data is null, type: " + request.getType());
        }

        List<Map<String, Object>> content = cmdbResponse.getData().getContent();

        if (content == null) {
            log.error("request cmdb error: response data content is null, code: [{}], type: [{}], url: [{}]", WetoolsExceptionCode.REQUEST_CMDB_ERROR, request.getType(), url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response data content is null, type: " + request.getType());
        }
    }

    private Object serializationResponse(String response) throws IOException {

        Object result;
        try {
            result = objectMapper.readValue(response, CmdbResponseError.class);
        } catch (IOException e) {
            result = objectMapper.readValue(response, CmdbResponse.class);
        }

        return result;
    }
}
