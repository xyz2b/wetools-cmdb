package com.webank.wetoolscmdb.config;

import com.webank.wetoolscmdb.utils.YmlPropertyResourceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Configuration("CmdbApiConfig")
@ConfigurationProperties(prefix = "cmdb")
@PropertySource(value = {"classpath:api.yml"}, ignoreResourceNotFound = false, encoding = "UTF-8", name = "config/api.yml", factory = YmlPropertyResourceFactory.class)
@Getter
@Setter
@ToString
@Validated
public class CmdbApiProperties {
    @NotEmpty
    private String url;
    @NotEmpty
    private String authUser;
    @Min(1)
    private int pageSize = 500;
}
