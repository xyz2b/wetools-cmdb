package com.webank.wetoolscmdb.utils.itsm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wetoolscmdb.config.ItsmApiProperties;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.constant.consist.ItsmApiConsist;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsRequest;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmResponse;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmResponseData;
import com.webank.wetoolscmdb.utils.exception.WetoolsCmdbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
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

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_YEAR = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_YEAR);

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<ItsmProblemsResponse>  getProblemsBySolveTeamAndCreateTime(List<Integer> handlerTeamIds, String createStartTime, String createEndTime) throws ParseException {
        String url = props.getUrl() + ITSM_EVENT_API_URL + "/getProblems.any";

        ItsmProblemsRequest itsmProblemsRequest = new ItsmProblemsRequest();
        itsmProblemsRequest.setHandlerTeamIds(handlerTeamIds);
        itsmProblemsRequest.setCurrentPage(1);
        itsmProblemsRequest.setPageSize(props.getPageSize());

        Date startTime = SIMPLE_DATE_FORMAT_SECOND.parse(createStartTime);
        itsmProblemsRequest.setCreateDateSearchStart(startTime.getTime());
        Date endTime = SIMPLE_DATE_FORMAT_SECOND.parse(createEndTime);
        itsmProblemsRequest.setCreateDateSearchEnd(endTime.getTime());

        ItsmResponseData itsmResponseData = queryItsm(url, itsmProblemsRequest);
        List<Map<String, Object>> response = itsmResponseData.getData();

        while (!isLastPage(itsmResponseData)) {
            itsmProblemsRequest.setCurrentPage(itsmResponseData.getCurrentPage() + 1);
            itsmResponseData = queryItsm(url, itsmProblemsRequest);
            response.addAll(itsmResponseData.getData());
        }

        if (response.size() == 0) {
            return null;
        }

        List<ItsmProblemsResponse> itsmProblemsResponses = new ArrayList<>();
        for(Map<String, Object> r : response) {
            Object son = r.get(ItsmApiConsist.PROBLEM_SAFE_EVENT_SON_VO_LIST);
            if (son != null) {
                List<Map<String, Object>> sonProblemList = (List<Map<String, Object>>) son;
                for(Map<String, Object> sonR : sonProblemList) {
                    ItsmProblemsResponse itsmProblemsResponse = parseItsmProblemData(sonR);
                    itsmProblemsResponses.add(itsmProblemsResponse);
                }
            } else {
                ItsmProblemsResponse itsmProblemsResponse = parseItsmProblemData(r);
                itsmProblemsResponses.add(itsmProblemsResponse);
            }
        }

        return itsmProblemsResponses;
    }

    private ItsmProblemsResponse parseItsmProblemData(Map<String, Object> r) {
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

        String solveUser = (String) r.getOrDefault(ItsmApiConsist.PROBLEM_SOLVE_USER, "");
        String solveUserName = (String) r.getOrDefault(ItsmApiConsist.PROBLEM_SOLVE_USER_NAME, "");
        String solveTeamName = (String) r.getOrDefault(ItsmApiConsist.PROBLEM_SOLVE_USER_TEAM_NAME, "");
        int solveTeamId = (int) r.getOrDefault(ItsmApiConsist.PROBLEM_SOLVE_USER_TEAM_ID, 0);

        ItsmProblemsResponse itsmProblemsResponse = new ItsmProblemsResponse(id, title, status, source, priority, create,
                plan, solve, solveUser, solveUserName, solveTeamName, solveTeamId);

        return itsmProblemsResponse;
    }

    private boolean isLastPage(ItsmResponseData itsmResponseData) {
        return itsmResponseData.getCurrentPage() == itsmResponseData.getTotalPage();
    }

    private ItsmResponseData queryItsm(String url, Object body) {
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

        return response.getData();
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
