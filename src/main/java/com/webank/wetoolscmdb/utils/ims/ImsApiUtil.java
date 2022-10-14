package com.webank.wetoolscmdb.utils.ims;

import com.webank.wetoolscmdb.config.ImsApiProperties;
import com.webank.wetoolscmdb.config.ItsmApiProperties;
import com.webank.wetoolscmdb.constant.consist.CmdbApiConsist;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.constant.enums.ImsAlertLevel;
import com.webank.wetoolscmdb.constant.enums.ImsAlertState;
import com.webank.wetoolscmdb.constant.enums.ImsDomainId;
import com.webank.wetoolscmdb.model.dto.ims.ImsAlarmListResponse;
import com.webank.wetoolscmdb.model.dto.ims.ImsComAlert;
import com.webank.wetoolscmdb.model.dto.ims.ImsScheduleMetricListResponse;
import com.webank.wetoolscmdb.model.dto.ims.ImsScheduleMetricListResponseData;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmResponseData;
import com.webank.wetoolscmdb.utils.exception.WetoolsCmdbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ImsApiUtil {
    @Autowired
    private ImsApiProperties props;
    @Autowired
    private RestTemplate rest;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_DAY);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_SECOND);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_MILLISECOND = new SimpleDateFormat(CmdbApiConsist.DATE_FORMAT_MILLISECOND);

    private final String IMS_SCHEDULE_METRIC_LIST_API_URL = "/ims_config/getImsScheduleMetricList.do";
    private final String IMS_ALARM_LIST_API_URL = "/ims_config/get_alarm_list.do";


    public List<ImsScheduleMetricListResponseData> getImsScheduleMetricList(List<String> ips, String subsystem, List<String> attrCodes) {
        String url = props.getUrl() + IMS_SCHEDULE_METRIC_LIST_API_URL;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("ip", String.join(",", ips));
        if(subsystem != null)  {
            builder.queryParam("subsystem", subsystem);
        }
        if(attrCodes != null && attrCodes.size() > 0) {
            builder.queryParam("attrCode", String.join(",", attrCodes));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<ImsScheduleMetricListResponse> response = rest.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                ImsScheduleMetricListResponse.class);

        if (response.getBody() == null) {
            log.error("request ims error: response body is null, url: [{}]", url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_IMS_ERROR, "request ims error: response body is null, url: " + url);
        }

        if (response.getBody().getCode() != 0) {
            log.error("request ims error: [{}], url: [{}], authUserKey: [{}]", response.getBody().getMsg(), url, props.getAuthUserKey());
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_IMS_ERROR, "request ims error: " + response.getBody().getMsg() + ", url: " + url);
        }

        return response.getBody().getData();
    }

    public List<ImsComAlert> getImsAlarmList(ImsAlertState alertState, List<ImsDomainId> domainIds, List<ImsAlertLevel> alertLevels, Date firstAlertStartTime, Date firstAlertEndTime, String env) {
        ImsAlarmListResponse imsAlarmListResponse = getImsAlarmList(alertState, domainIds, alertLevels, firstAlertStartTime, firstAlertEndTime, props.getPageSize().get(env), 0, env);
        List<ImsComAlert> imsComAlertList = new ArrayList<>(imsAlarmListResponse.getComAlertLists());
        while (!isImsAlarmListLastPage(imsAlarmListResponse)) {
            int curPageIndex = imsAlarmListResponse.getCurrPageNum();
            imsAlarmListResponse = getImsAlarmList(alertState, domainIds, alertLevels, firstAlertStartTime, firstAlertEndTime, props.getPageSize().get(env), curPageIndex + 1, env);
            imsComAlertList.addAll(imsAlarmListResponse.getComAlertLists());
        }
        return imsComAlertList;
    }

    public ImsAlarmListResponse getImsAlarmList(ImsAlertState alertState, List<ImsDomainId> domainIds, List<ImsAlertLevel> alertLevels, Date firstAlertStartTime, Date firstAlertEndTime, int pageSize, int reqPageIndex, String env) {
        String url = props.getUrl() + IMS_ALARM_LIST_API_URL;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("userAuthKey", props.getAuthUserKey().get(env));
        map.add("alertState", Integer.toString(alertState.getValue()));
        map.add("pageSize", Integer.toString(pageSize));
        map.add("reqPageIndex", Integer.toString(reqPageIndex));

        List<String> domainIdsStr = new ArrayList<>(domainIds.size());
        for(ImsDomainId imsDomainId : domainIds) {
            domainIdsStr.add(Long.toString(imsDomainId.getValue()));
        }
        map.add("domainIds", String.join(",", domainIdsStr));

        List<String> alertLevelsStr = new ArrayList<>(alertLevels.size());
        for(ImsAlertLevel imsAlertLevel : alertLevels) {
            alertLevelsStr.add(Integer.toString(imsAlertLevel.getValue()));
        }
        map.add("alertLevels", String.join(",", alertLevelsStr));
        map.add("firstAlertStartTime", SIMPLE_DATE_FORMAT_SECOND.format(firstAlertStartTime));
        map.add("firstAlertEndTime", SIMPLE_DATE_FORMAT_SECOND.format(firstAlertEndTime));

        // 封装请求参数
        HttpEntity<MultiValueMap<String, String>> requestb = new HttpEntity<MultiValueMap<String, String>>(map,
                headers);
        ResponseEntity<ImsAlarmListResponse> response = rest.postForEntity(url, requestb, ImsAlarmListResponse.class);

        if (response.getBody() == null) {
            log.error("request ims error: response body is null, url: [{}]", url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_IMS_ERROR, "request ims error: response body is null, url: " + url);
        }

        if (response.getBody().getErrorCode() != 0) {
            log.error("request ims error: [{}], url: [{}], authUserKey: [{}]", response.getBody().getErrorMessage(), url, props.getAuthUserKey());
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_IMS_ERROR, "request ims error: " + response.getBody().getErrorMessage() + ", url: " + url);
        }

        return response.getBody();
    }

    private boolean isImsAlarmListLastPage(ImsAlarmListResponse imsAlarmListResponse) {
        return imsAlarmListResponse.getCurrPageNum() + 1 == imsAlarmListResponse.getTotalPageNum();
    }
}