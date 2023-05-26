package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ci {
    private String id;
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
    @JsonProperty("syn_cmdb_cycle")
    @Min(value = 1000)
    private int synCmdbCycle;
    @JsonProperty("field_list")
    private List<CiField> fieldList;
    @JsonProperty("ci_data_last_update_date")
    private String ciDataLastUpdateDate;
    @JsonProperty("created_date")
    private String createdDate;
    @JsonProperty("updated_date")
    private String updatedDate;
    @JsonProperty("updated_by")
    private String updatedBy;
    @JsonProperty("created_by")
    private String createdBy;
    @JsonProperty("cron_id")
    private Long cronId;
}
