package com.webank.wetoolscmdb.model.entity.mongo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@ToString
public class FiledDao implements Serializable {
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
    @Field("is_display")
    private Boolean isDisplay;
    @Field("type")
    private String type;
    @Field("predict_length")
    private int predictLength;
    @Field("ci")
    private String ci;
    @Field("created_date")
    @CreatedDate
    private Date createdDate;
    @Field("updated_date")
    @LastModifiedDate
    private Date updatedDate;
    @Field("updated_by")
    private String updatedBy;
    @Field("created_by")
    private String createdBy;
}
