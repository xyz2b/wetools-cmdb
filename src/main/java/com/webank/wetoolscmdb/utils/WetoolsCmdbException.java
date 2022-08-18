package com.webank.wetoolscmdb.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WetoolsCmdbException extends RuntimeException {
    private int code;
    private String errMsg;
}
