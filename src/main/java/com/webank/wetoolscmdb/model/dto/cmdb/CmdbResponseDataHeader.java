package com.webank.wetoolscmdb.model.dto.cmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CmdbResponseDataHeader {
    private String permissionType;
    @JsonProperty("is_system")
    private String isSystem;
    @JsonProperty("complete_rate")
    private String completeRate;
    @JsonProperty("search_seq")
    private int searchSeq;
    /**
     * 如果dataType是ref、multiRef，则content中对应字段的内容就是kv形式的列表
     * 如果dataType是select、text、textArea，则content中对应字段的内容就是一个单纯的字符串值
     * 如果dataType是number，则content中对应的字段内容就是数字
     * */
    private String dataType;
    private int refType;
    private List<Map<String, String>> refList;
    @JsonProperty("is_unique")
    private String isUnique;
    private String description;
    @JsonProperty("display_type")
    private String displayType;
    @JsonProperty("display_seq")
    private int displaySeq;
    private int idAdmCiTypeAttr;
    @JsonProperty("is_none")
    private String isNone;
    private String enName;
    private String name;
    private String refUrl;
}
