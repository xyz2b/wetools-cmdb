package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiField {
    private String id;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    @NotEmpty
    private String enName;
    @JsonProperty("is_cmdb")
    @NonNull
    private Boolean isCmdb;
    @JsonProperty("is_display")
    private Boolean isDisplay;
    @JsonProperty("type")
    private String type;
    @JsonProperty("predict_length")
    private int predictLength;
    @JsonProperty("created_date")
    private String createdDate;
    @JsonProperty("updated_date")
    private String updatedDate;
    @JsonProperty("updated_by")
    private String updatedBy;
    @JsonProperty("created_by")
    private String createdBy;
}
