package com.webank.wetoolscmdb.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Getter
@Setter
public class MongoBathUpdateOptions {
    private Query query;
    private Update update;
    private boolean upsert = true;
    private boolean multi = false;
}
