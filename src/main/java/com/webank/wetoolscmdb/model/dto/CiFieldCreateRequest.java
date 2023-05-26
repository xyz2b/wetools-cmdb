package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiFieldCreateRequest {
    @JsonProperty("ci_name")
    @NotEmpty
    private String ciName;
    @NotEmpty
    private String env;
    @JsonProperty("ci_field_list")
    @NotEmpty
    private List<CiField> ciFields;
    private Map<String, Object> filter;
}
