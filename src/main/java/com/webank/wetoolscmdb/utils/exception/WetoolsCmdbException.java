package com.webank.wetoolscmdb.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class WetoolsCmdbException extends RuntimeException {
    private int code;
    private String errMsg;

    public WetoolsCmdbException(int code, String errMsg) {
        super(errMsg);
        this.code = code;
    }
}
