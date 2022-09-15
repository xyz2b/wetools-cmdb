package com.webank.wetoolscmdb.utils.itsm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.config.CmdbApiProperties;
import com.webank.wetoolscmdb.config.ItsmApiProperties;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.constant.consist.ItsmApiConsist;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponse;
import com.webank.wetoolscmdb.model.dto.cmdb.CmdbResponseError;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsRequest;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmResponse;
import com.webank.wetoolscmdb.utils.exception.WetoolsCmdbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ItsmApiUtil {
    @Autowired
    private ItsmApiProperties props;
    @Autowired
    private RestTemplate rest;

    private final String ITSM_EVENT_API_URL = "/itsm/event";

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<ItsmProblemsResponse>  getProblems(ItsmProblemsRequest itsmProblemsRequest) throws JsonProcessingException {
        String url = props.getUrl() + ITSM_EVENT_API_URL + "/getProblems.any";

        log.debug("request [{}] body [{}]", url, itsmProblemsRequest);

        List<Map<String, Object>> response = queryItsm(url, itsmProblemsRequest);

        if (response.size() == 0) {
            return null;
        }

        List<ItsmProblemsResponse> itsmProblemsResponses = new ArrayList<>();
        for(Map<String, Object> r : response) {
            long id = (int) r.getOrDefault(ItsmApiConsist.ID, 0);
            String title = (String) r.getOrDefault(ItsmApiConsist.PROBLEM_TITLE, "");
            String status = (String) r.getOrDefault(ItsmApiConsist.PROBLEM_STATUS, 0);
            String source = (String) r.getOrDefault(ItsmApiConsist.PROBLEM_SOURCE_NAME, "");
            String priority = (String) r.getOrDefault(ItsmApiConsist.PROBLEM_PRIORITY_LEVEL, "");

            Object createDate = r.get(ItsmApiConsist.PROBLEM_CREATE_DATE);
            String create = null;
            if(createDate != null) {
                create = SIMPLE_DATE_FORMAT_MILLISECOND.format(new Date((long)createDate));
            }

            Object planDate = r.get(ItsmApiConsist.PROBLEM_PLAN_DATE);
            String plan = null;
            if(planDate != null) {
                plan = SIMPLE_DATE_FORMAT_MILLISECOND.format(new Date((long)planDate));
            }

            Object solveDate = r.get(ItsmApiConsist.PROBLEM_SOLVE_DATE);
            String solve = null;
            if(solveDate != null) {
                solve = SIMPLE_DATE_FORMAT_MILLISECOND.format(new Date((long)solveDate));
            }

            String solveUser = (String) r.getOrDefault(ItsmApiConsist.SOLVE_HANDLER, "");
            String solveUserName = (String) r.getOrDefault(ItsmApiConsist.SOLVE_HANDLER_NAME, "");

            ItsmProblemsResponse itsmProblemsResponse = new ItsmProblemsResponse(id, title, status, source, priority, create,
                    plan, solve, solveUser, solveUserName);
            itsmProblemsResponses.add(itsmProblemsResponse);
        }

        return itsmProblemsResponses;
    }

    private List<Map<String, Object>> queryItsm(String url, Object body) {
        long currentTimestamp = System.currentTimeMillis();
        int random = (int) ((Math.random()*9+1)*1000);

        String sign = calSign(currentTimestamp, random, props.getAuthUser(), props.getAppKey());

        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.set("appId", props.getAppid());
        headers.set("appKey", props.getAppKey());
        headers.set("random", String.valueOf(random));
        headers.set("timestamp", String.valueOf(currentTimestamp));
        headers.set("userId",  props.getAuthUser());
        headers.set("sign", sign);

        HttpEntity<Object> entity = new HttpEntity<Object>(body, headers);

        ItsmResponse response = rest.postForObject(url, entity, ItsmResponse.class);

        checkResponse(response, url);

        return response.getData().getData();
    }

    private void checkResponse(ItsmResponse response, String url) {
        if (response == null) {
            log.error("request itsm error: response is null, url: [{}]", url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_ITSM_ERROR, "request itsm error: response is null, url: " + url);
        }

        if (response.getRetCode() != 0) {
            log.error("request itsm error: [{}], url: [{}], user: [{}], appid: [{}]", response.getRetDetail(), url, props.getAuthUser(), props.getAppid());
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_ITSM_ERROR, "request itsm error: " + response.getRetDetail() + ", url: " + url);
        }

        if (response.getData() == null) {
            log.error("itsm response data is null, url: [{}]", url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_ITSM_ERROR, "itsm response data is null, url: " + url);
        }
    }

    private String calSign(long currentTimestamp, int random, String user, String appKey) {
        String m1 = DigestUtils.md5DigestAsHex((appKey + random + currentTimestamp).getBytes());
        String m2 = DigestUtils.md5DigestAsHex((m1 + user).getBytes());

        return m2;
    }
}
