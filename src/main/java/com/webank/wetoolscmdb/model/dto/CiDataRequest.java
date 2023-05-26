package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiDataRequest {
    @JsonProperty("ci_name")
    @NotEmpty
    private String ciName;
    @JsonProperty("env")
    @NotEmpty
    private String env;
    @JsonProperty("result_column")
    private List<String> resultColumn;
    @JsonProperty("filter")
    private Map<String, Object> filter;
}
