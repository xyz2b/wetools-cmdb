package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiData {
    private String id;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("env")
    private String env;
    private Map<String, Object> data;
}
