package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiRequest {
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    @NotEmpty
    private String enName;
    @JsonProperty("is_cmdb")
    @NonNull
    private Boolean isCmdb;
    @JsonProperty("env")
    @NotEmpty
    private String env;
    @JsonProperty("filter")
    private Map<String, Object> filter;
    @JsonProperty("syn_cmdb_cycle")
    @Min(value = 1000)
    private int synCmdbCycle;
    @JsonProperty("field_list")
    private List<CiField> fieldList;
}
