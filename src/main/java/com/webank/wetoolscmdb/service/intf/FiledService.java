package com.webank.wetoolscmdb.service.intf;

import com.webank.wetoolscmdb.model.dto.CiFiled;

import java.util.List;

public interface FiledService {
    boolean createFiled(List<CiFiled> filed, String belongCiId, String env);
}
