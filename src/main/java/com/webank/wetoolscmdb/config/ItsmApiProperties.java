package com.webank.wetoolscmdb.config;

import com.webank.wetoolscmdb.utils.YmlPropertyResourceFactory;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Configuration("ItsmApiProperties")
@ConfigurationProperties(prefix = "itsm")
@PropertySource(value = {"classpath:api.yml"}, ignoreResourceNotFound = false, encoding = "UTF-8", name = "api.yml", factory = YmlPropertyResourceFactory.class)
@Getter
@Setter
@ToString
@Validated
public class ItsmApiProperties {
    private String url;
    private String authUser;
    private String appKey;
    private String appid;
    private List<Integer> otpdTeamIds;
    private int pageSize;
}
