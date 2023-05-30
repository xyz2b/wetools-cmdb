package com.webank.wetoolscmdb.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.bson.Document;

import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiDataResponse {
    @JsonProperty("last_id")
    private String lastId;
    @JsonProperty("page_size")
    private int pageSize;
    @JsonProperty("total_rows")
    private long totalRows;
    @JsonProperty("return_rows")
    private long returnRows;
    @JsonProperty("fields")
    private List<Document> ciFieldList;
    @JsonProperty("data")
    private List<Map<String, Object>> cIData;
}
