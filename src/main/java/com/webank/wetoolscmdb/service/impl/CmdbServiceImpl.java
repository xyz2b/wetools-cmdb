package com.webank.wetoolscmdb.service.impl;

import com.webank.wetoolscmdb.service.intf.CmdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CmdbServiceImpl implements CmdbService {
    @Override
    public void syncCmdbAllData(String type) {

    }

    @Override
    public void syncManyColumnCmdbData(String type, List<String> resultColumn) {

    }

    @Override
    public void syncManyColumnCmdbDataByFilter(String type, Map<String, String> filter, List<String> resultColumn) {

    }
}
