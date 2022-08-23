package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiField {
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("is_cmdb")
    private Boolean isCmdb;
    @JsonProperty("is_display")
    private Boolean isDisplay;
    @JsonProperty("type")
    private String type;
    @JsonProperty("predict_length")
    private int predictLength;
}
