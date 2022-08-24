package com.webank.wetoolscmdb.service.impl;

import com.webank.wetoolscmdb.service.intf.CronService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.request.http.SaveJobInfoRequest;
import tech.powerjob.common.response.ResultDTO;

@Service
public class CronServiceImpl implements CronService {
    @Autowired
    PowerJobClient powerJobClient;

    @Override
    public Long createJob(SaveJobInfoRequest request) {
        ResultDTO<Long> resultDTO = powerJobClient.saveJob(request);
        if(resultDTO.isSuccess()) {
            return resultDTO.getData();
        } else {
            return null;
        }
    }
}
