package com.webank.wetoolscmdb.utils;

import com.webank.wetoolscmdb.config.CmdbApiProperties;
import com.webank.wetoolscmdb.constant.consist.CmdbApi;
import com.webank.wetoolscmdb.constant.consist.CmdbQueryDataType;
import com.webank.wetoolscmdb.constant.consist.WetoolsCmdbExceptionCode;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbRequest;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponse;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseData;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseDataHeader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CmdbClient {
    @Autowired
    private CmdbApiProperties props;
    @Autowired
    private RestTemplate rest;

    private final String CMDB_API_URL = "/cmdb/api/";

    // 获取某个CI所有字段的属性信息
    public Map<String, CmdbResponseDataHeader> getCiFiledAttributes(String type) {
        return getCiFiledAttributes(type, new ArrayList<>(0));
    }

    // 获取某个CI指定字段的属性信息
    // guid、created_by、created_date、updated_date是默认属性，指定不指定这四个字段都会返回这四个字段的属性信息
    public Map<String, CmdbResponseDataHeader> getCiFiledAttributes(String type, List<String> resultColumn) {
        CmdbResponse response = standardQueryCmdb(type, 0, 0, true, new HashMap<>(0), resultColumn);
        Map<String, CmdbResponseDataHeader> ciFiledAttributes = new HashMap<>(response.getData().getHeader().size());
        for(CmdbResponseDataHeader header : response.getData().getHeader()) {
            ciFiledAttributes.put(header.getEnName(), header);
        }
        return ciFiledAttributes;
    }

    // 获取综合查询模板所有字段的属性信息
    public Map<String, CmdbResponseDataHeader> getTemplateFiledAttributes(String type) {
        return getTemplateFiledAttributes(type, new ArrayList<>(0));
    }

    // 获取综合查询模板指定字段的属性信息
    // 目前CMDB并未对综合查询模板的指定字段进行过滤，不管是否指定字段，都会返回所有字段的属性信息
    public Map<String, CmdbResponseDataHeader> getTemplateFiledAttributes(String type, List<String> resultColumn) {
        CmdbResponse response = templateQueryCmdb(type, 0, 0, true, new HashMap<>(0), resultColumn);
        Map<String, CmdbResponseDataHeader> ciFiledAttributes = new HashMap<>(response.getData().getHeader().size());
        for(CmdbResponseDataHeader header : response.getData().getHeader()) {
            ciFiledAttributes.put(header.getEnName(), header);
        }
        return ciFiledAttributes;
    }

    // 获取某个CI的所有数据
    public CmdbResponseData getCiData(String type) {
        CmdbResponse firstResponse = standardQueryCmdb(type, 0, props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

        CmdbResponse response = firstResponse;
        while (!isLastPage(response)) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个CI指定 过滤条件、返回字段 的所有数据
    public CmdbResponseData getCiData(String type, Map<String, String> filter, List<String> resultColumn) {
        CmdbResponse firstResponse = standardQueryCmdb(type, 0, props.getPageSize(),true, filter, resultColumn);

        CmdbResponse response = firstResponse;
        while (!isLastPage(response)) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个CI指定 过滤条件 的所有数据
    public CmdbResponseData getCiData(String type, Map<String, String> filter) {
        CmdbResponse firstResponse = standardQueryCmdb(type, 0, props.getPageSize(),true, filter, new ArrayList<>(0));

        CmdbResponse response = firstResponse;
        while (!isLastPage(response)) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个CI指定 返回字段 的所有数据
    public CmdbResponseData getCiData(String type, List<String> resultColumn) {
        CmdbResponse firstResponse = standardQueryCmdb(type, 0, props.getPageSize(),true, new HashMap<>(0), resultColumn);

        CmdbResponse response = firstResponse;
        while (!isLastPage(response)) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个CI指定 过滤条件、返回字段、返回数量(startIndex, pageSize) 的数据
    public CmdbResponseData getCiData(String type, int startIndex, int pageSize, Map<String, String> filter, List<String> resultColumn) {
        CmdbResponse response = standardQueryCmdb(type, startIndex, pageSize, true, filter, resultColumn);
        return response.getData();
    }

    // 判断是不是最后一页数据
    private boolean isLastPage(CmdbResponse response) {
        // 本次获取的startIndex加上本次获取的分页大小，就是下一次要获取的startIndex，如果下一次startIndex已经超出了总数据量，就说明获取了全部的数据了
        int nextStartIndex = Integer.parseInt(response.getHeaders().getStartIndex()) + response.getHeaders().getContentRows();
        return nextStartIndex >= Integer.parseInt(response.getHeaders().getTotalRows());
    }

    // 获取某个 Template 的所有数据
    public CmdbResponseData getTemplateData(String type) {
        CmdbResponse firstResponse = templateQueryCmdb(type, 0, props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

        CmdbResponse response = firstResponse;
        // 由于CMDB 综合查询接口 实际返回行数有问题，只能下面这样判断
        while (response.getHeaders().getContentRows() != 0) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个 Template 指定 过滤条件、返回字段 的所有数据
    public CmdbResponseData getTemplateData(String type, Map<String, String> filter, List<String> resultColumn) {
        CmdbResponse firstResponse = templateQueryCmdb(type, 0, props.getPageSize(),true, filter, resultColumn);

        CmdbResponse response = firstResponse;
        // 由于CMDB 综合查询接口 实际返回行数有问题，只能下面这样判断
        while (response.getHeaders().getContentRows() != 0) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个 Template 指定 过滤条件 的所有数据
    public CmdbResponseData getTemplateData(String type, Map<String, String> filter) {
        CmdbResponse firstResponse = templateQueryCmdb(type, 0, props.getPageSize(),true, filter, new ArrayList<>(0));

        CmdbResponse response = firstResponse;
        // 由于CMDB 综合查询接口 实际返回行数有问题，只能下面这样判断
        while (response.getHeaders().getContentRows() != 0) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个 Template 指定 返回字段 的所有数据
    public CmdbResponseData getTemplateData(String type, List<String> resultColumn) {
        CmdbResponse firstResponse = templateQueryCmdb(type, 0, props.getPageSize(),true, new HashMap<>(0), resultColumn);

        CmdbResponse response = firstResponse;
        // 由于CMDB 综合查询接口 实际返回行数有问题，只能下面这样判断
        while (response.getHeaders().getContentRows() != 0) {
            response = standardQueryCmdb(type, Integer.parseInt(response.getHeaders().getStartIndex()) + props.getPageSize(), props.getPageSize(),true, new HashMap<>(0), new ArrayList<>(0));

            firstResponse.getData().getContent().addAll(response.getData().getContent());
        }

        return firstResponse.getData();
    }

    // 获取某个Template指定 过滤条件、返回字段、返回数量(startIndex, pageSize) 的数据
    public CmdbResponseData getTemplateData(String type, int startIndex, int pageSize, Map<String, String> filter, List<String> resultColumn) {
        CmdbResponse response = templateQueryCmdb(type, startIndex, pageSize, true, filter, resultColumn);
        return response.getData();
    }

    // CMDB统一查询接口
    private CmdbResponse standardQueryCmdb(String type, int startIndex, int pageSize, boolean isPaging,
                                                  Map<String, String> filter, List<String> resultColumn) {
        String url = props.getUrl() + CMDB_API_URL + CmdbQueryDataType.STANDARD_QUERY + ".json";
        return queryCmdb(url, type, startIndex, pageSize, isPaging, filter, resultColumn);
    }

    // CMDB综合查询接口
    private CmdbResponse templateQueryCmdb(String type, int startIndex, int pageSize, boolean isPaging,
                                                  Map<String, String> filter, List<String> resultColumn) {
        String url = props.getUrl() + CMDB_API_URL + CmdbQueryDataType.TEMPLATE_QUERY + ".json";
        return queryCmdb(url, type, startIndex, pageSize, isPaging, filter, resultColumn);
    }

    // 查询CMDB数据的基础方法
    private CmdbResponse queryCmdb(String url, String type, int startIndex, int pageSize, boolean isPaging,
                                 Map<String, String> filter, List<String> resultColumn) {
        CmdbRequest request = new CmdbRequest(props.getAuthUser(), type, startIndex, pageSize, CmdbApi.ACTION_SELECT, isPaging, filter, resultColumn);

        CmdbResponse response = rest.postForObject(url, request, CmdbResponse.class);

        checkCmdbResponse(response, request);

        return response;
    }

    private static void checkCmdbResponse(CmdbResponse response, CmdbRequest request) {
        if (response == null) {
            log.error("request cmdb error: response is null, type: " + request.getType());
            throw new WetoolsCmdbException(WetoolsCmdbExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response is null, type: " + request.getType());
        }

        if (response.getHeaders() == null) {
            log.error("request cmdb error: response headers is null, type: " + request.getType());
            throw new WetoolsCmdbException(WetoolsCmdbExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response headers is null, type: " + request.getType());
        }

        if (response.getHeaders().getRetCode() != 0) {
            log.error("request cmdb error: " + response.getHeaders().getErrorInfo() + ", type: " + request.getType());
            throw new WetoolsCmdbException(WetoolsCmdbExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: " + response.getHeaders().getErrorInfo() + ", type: " + request.getType());
        }

        if (response.getData() == null) {
            log.error("request cmdb error: response data is null, type: " + request.getType());
            throw new WetoolsCmdbException(WetoolsCmdbExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response data is null, type: " + request.getType());
        }

        List<Map<String, Object>> content = response.getData().getContent();

        if (content == null) {
            log.error("request cmdb error: response data content is null, type: " + request.getType());
            throw new WetoolsCmdbException(WetoolsCmdbExceptionCode.REQUEST_CMDB_ERROR, "request cmdb error: response data content is null, type: " + request.getType());
        }
    }

}
