package com.webank.wetoolscmdb.model.entity.mongo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@ToString
public class CiDao implements Serializable {
    private static final long serialVersionUID = -3258839839160856613L;

    @Id
    @Field("_id")
    private String id;
    @Field("cn_name")
    private String cnName;
    @Field("en_name")
    private String enName;
    @Field("is_delete")
    private Boolean isDelete;
    @Field("is_cmdb")
    private Boolean isCmdb;
    @Field("ci_data_last_update_date")
    @JsonFormat( pattern ="yyyy-MM-dd HH:mm:ss", timezone ="GMT+8")
    private Date cIDataLastUpdateDate;
    @Field("syn_cmdb_cycle")
    private int synCmdbCycle;
    @Field("is_updating")
    private Boolean isUpdating;
    @Field("created_date")
    @JsonFormat( pattern ="yyyy-MM-dd HH:mm:ss", timezone ="GMT+8")
    private Date createdDate;
    @Field("updated_date")
    @JsonFormat( pattern ="yyyy-MM-dd HH:mm:ss", timezone ="GMT+8")
    private Date updatedDate;
    @Field("updated_by")
    private String updatedBy;
    @Field("created_by")
    private String createdBy;
}
