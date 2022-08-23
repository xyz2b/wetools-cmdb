package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiField {
    @Field("cn_name")
    private String cnName;
    @Field("en_name")
    private String enName;
    @Field("is_cmdb")
    private Boolean isCmdb;
    @Field("is_display")
    private Boolean isDisplay;
    @Field("type")
    private String type;
    @Field("predict_length")
    private int predictLength;
}
