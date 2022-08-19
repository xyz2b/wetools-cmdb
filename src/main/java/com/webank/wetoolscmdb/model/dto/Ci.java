package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @Field("cn_name")
    private String cnName;
    @Field("en_name")
    private String enName;
    @Field("is_cmdb")
    private Boolean isCmdb;
    @Field("env")
    private String env;
    @Field("syn_cmdb_cycle")
    private int synCmdbCycle;
    @Field("filed_list")
    private List<CiFiled> filedList;
}
