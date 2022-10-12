package com.webank.wetoolscmdb.config;

import com.webank.wetoolscmdb.utils.YmlPropertyResourceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Configuration("ImsApiConfig")
@ConfigurationProperties(prefix = "ims")
@PropertySource(value = {"classpath:api.yml"}, ignoreResourceNotFound = false, encoding = "UTF-8", name = "api.yml", factory = YmlPropertyResourceFactory.class)
@Getter
@Setter
@ToString
@Validated
public class ImsApiProperties {
    @NotNull
    private Map<String, String> url;
    @NotNull
    private Map<String, String> authUserKey;
}
