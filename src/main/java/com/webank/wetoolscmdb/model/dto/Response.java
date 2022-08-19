package com.webank.wetoolscmdb.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private int retCode;
    private String retDetail;
    private Object data;
}
