package com.webank.wetoolscmdb.service.impl;

import com.webank.wetoolscmdb.mapper.intf.mongo.FieldRepository;
import com.webank.wetoolscmdb.mapper.intf.mongo.ItsmProblemsRepository;
import com.webank.wetoolscmdb.model.dto.itsm.ItsmProblemsResponse;
import com.webank.wetoolscmdb.model.entity.mongo.ItsmProblemsDao;
import com.webank.wetoolscmdb.service.intf.ItsmProblemsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class itsmProblemsServiceImpl implements ItsmProblemsService {
    @Autowired
    ItsmProblemsRepository itsmProblemsRepository;

    @Override
    public int insertAll(List<ItsmProblemsResponse> itsmProblemsResponses) {
        if(!itsmProblemsRepository.problemCollectionExisted()) {
            itsmProblemsRepository.createProblemCollection();
        }

        List<ItsmProblemsDao> itsmProblemsDaos = new ArrayList<>();

        for(ItsmProblemsResponse itsmProblemsResponse : itsmProblemsResponses) {
            itsmProblemsDaos.add(ItsmProblemsDao.transfer(itsmProblemsResponse));
        }

        List<ItsmProblemsDao> rst = itsmProblemsRepository.insertAll(itsmProblemsDaos);
        return rst.size();
    }

    @Override
    public String findLastProblemCreateTime() {
        ItsmProblemsDao itsmProblemsDao = itsmProblemsRepository.findLastProblem();
        if(itsmProblemsDao == null) {
            return null;
        }
        return itsmProblemsDao.getCreateDate();
    }
}
