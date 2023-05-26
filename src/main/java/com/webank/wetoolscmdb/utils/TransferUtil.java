package com.webank.wetoolscmdb.utils;

import com.webank.wetoolscmdb.model.dto.Ci;
import com.webank.wetoolscmdb.model.dto.CiRequest;
import com.webank.wetoolscmdb.model.dto.CiField;
import com.webank.wetoolscmdb.model.entity.mongo.CiDao;
import com.webank.wetoolscmdb.model.entity.mongo.FieldDao;

public class TransferUtil {
    public static void transferFieldDaoToCiField(FieldDao fieldDao, CiField ciField) {
        ciField.setEnName(fieldDao.getEnName());
        ciField.setIsCmdb(fieldDao.getIsCmdb());
        ciField.setIsDisplay(fieldDao.getIsDisplay());
        ciField.setCnName(fieldDao.getCnName());
        ciField.setCreatedBy(fieldDao.getCreatedBy());
        ciField.setUpdatedBy(fieldDao.getUpdatedBy());
        ciField.setCreatedDate(fieldDao.getCreatedDate());
        ciField.setUpdatedDate(fieldDao.getUpdatedDate());
        ciField.setId(fieldDao.getId());
    }

    public static void transferCiDaoToCi(CiDao ciDao, Ci ci) {
        ci.setEnName(ciDao.getEnName());
        ci.setCnName(ciDao.getCnName());
        ci.setCiDataLastUpdateDate(ciDao.getCIDataLastUpdateDate());
        ci.setSynCmdbCycle(ciDao.getSynCmdbCycle());
        ci.setCronId(ciDao.getCronId());
        ci.setCreatedDate(ciDao.getCreatedDate());
        ci.setUpdatedDate(ciDao.getUpdatedDate());
        ci.setUpdatedBy(ciDao.getUpdatedBy());
        ci.setCreatedBy(ciDao.getCreatedBy());
        ci.setCiDataLastUpdateDate(ciDao.getCIDataLastUpdateDate());
        ci.setId(ciDao.getId());
    }

    public static void transferCiToCiDao(CiRequest ci, CiDao ciDao) {
        if(ci.getIsCmdb()) {
            ciDao.setSynCmdbCycle(ci.getSynCmdbCycle());
        }
        ciDao.setIsCmdb(ci.getIsCmdb());
        ciDao.setEnName(ci.getEnName());
        ciDao.setCnName(ci.getCnName());
    }

    public static void transferCiFieldToFieldDao(CiField ciField, FieldDao fieldDao) {
        fieldDao.setCnName(ciField.getCnName());
        fieldDao.setEnName(ciField.getEnName());
        fieldDao.setIsCmdb(ciField.getIsCmdb());
        fieldDao.setIsDisplay(ciField.getIsDisplay());
        fieldDao.setPredictLength(ciField.getPredictLength());
        fieldDao.setType(ciField.getType());
    }
}
