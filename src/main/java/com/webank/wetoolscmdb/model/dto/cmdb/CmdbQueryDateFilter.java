package com.webank.wetoolscmdb.model.dto.cmdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CmdbQueryDateFilter {
    private Map<String, String> range;
}
