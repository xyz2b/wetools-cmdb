package com.webank.wetoolscmdb.model.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.webank.wetoolscmdb.constant.consist.CmdbQueryResponseDataType;
import com.webank.wetoolscmdb.utils.exception.WetoolsCmdbException;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmdbResponseData {
    private List<CmdbResponseDataHeader> header;
    private List<Map<String, Object>> content;

    public List<Map<String, Object>> parseCmdbResponseData(CmdbResponseData cmdbResponseData) {
        // 字段名称 --> 字段属性
        Map<String, CmdbResponseDataHeader> cmdbResponseDataHeaders = new HashMap<>();
        for(CmdbResponseDataHeader cmdbResponseDataHeader : header) {
            cmdbResponseDataHeaders.put(cmdbResponseDataHeader.getEnName(), cmdbResponseDataHeader);
        }

        for(Map<String, Object> c : content) {
            for(Map.Entry<String, Object> entry : c.entrySet()) {
                if(cmdbResponseDataHeaders.get(entry.getKey()).getDataType().equals(CmdbQueryResponseDataType.REF)) {

                } else if (cmdbResponseDataHeaders.get(entry.getKey()).getDataType().equals(CmdbQueryResponseDataType.MULTI_REF)) {

                } else if (cmdbResponseDataHeaders.get(entry.getKey()).getDataType().equals(CmdbQueryResponseDataType.SELECT)) {

                } else if (cmdbResponseDataHeaders.get(entry.getKey()).getDataType().equals(CmdbQueryResponseDataType.TEXT)) {

                } else if (cmdbResponseDataHeaders.get(entry.getKey()).getDataType().equals(CmdbQueryResponseDataType.TEXTAREA)) {

                } else if (cmdbResponseDataHeaders.get(entry.getKey()).getDataType().equals(CmdbQueryResponseDataType.NUMBER)) {

                } else {
                    throw new WetoolsCmdbException(1, "unknown cmdb response data type");
                }
            }
        }

        return null;
    }
}
