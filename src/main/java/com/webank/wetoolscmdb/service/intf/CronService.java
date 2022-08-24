package com.webank.wetoolscmdb.service.intf;

import tech.powerjob.common.request.http.SaveJobInfoRequest;

public interface CronService {
    Long createJob(SaveJobInfoRequest request);
}
