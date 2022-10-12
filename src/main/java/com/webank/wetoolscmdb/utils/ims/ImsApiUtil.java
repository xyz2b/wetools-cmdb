package com.webank.wetoolscmdb.utils.ims;

import com.webank.wetoolscmdb.config.ImsApiProperties;
import com.webank.wetoolscmdb.config.ItsmApiProperties;
import com.webank.wetoolscmdb.constant.consist.WetoolsExceptionCode;
import com.webank.wetoolscmdb.model.dto.ims.ImsScheduleMetricListResponse;
import com.webank.wetoolscmdb.model.dto.ims.ImsScheduleMetricListResponseData;
import com.webank.wetoolscmdb.utils.exception.WetoolsCmdbException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Service
public class ImsApiUtil {
    @Autowired
    private ImsApiProperties props;
    @Autowired
    private RestTemplate rest;

    private final String IMS_SCHEDULE_METRIC_LIST_API_URL = "/ims_config/getImsScheduleMetricList.do";

    public List<ImsScheduleMetricListResponseData> getImsScheduleMetricList(List<String> ips, String subsystem, List<String> attrCodes) {
        String url = props.getUrl() + IMS_SCHEDULE_METRIC_LIST_API_URL;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("ip", String.join(",", ips))
                .queryParam("subsystem", subsystem)
                .queryParam("attrCode", String.join(",", attrCodes));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<ImsScheduleMetricListResponse> response = rest.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                entity,
                ImsScheduleMetricListResponse.class);

        if(response.getBody() == null) {
            log.error("request ims error: response body is null, url: [{}]", url);
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_IMS_ERROR, "request ims error: response body is null, url: " + url);
        }

        if(response.getBody().getCode() != 0) {
            log.error("request ims error: [{}], url: [{}], authUserKey: [{}]", response.getBody().getMsg(), url, props.getAuthUserKey());
            throw new WetoolsCmdbException(WetoolsExceptionCode.REQUEST_IMS_ERROR, "request ims error: " + response.getBody().getMsg() + ", url: " + url);
        }

        return response.getBody().getData();
    }
}
