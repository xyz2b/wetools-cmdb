package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ci {
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("en_name")
    private String enName;
    @JsonProperty("is_cmdb")
    private Boolean isCmdb;
    @JsonProperty("env")
    private String env;
    @JsonProperty("syn_cmdb_cycle")
    private int synCmdbCycle;
    @JsonProperty("filed_list")
    private List<CiField> filedList;
}
