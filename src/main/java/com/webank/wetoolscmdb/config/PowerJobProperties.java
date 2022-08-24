package com.webank.wetoolscmdb.config;

import com.webank.wetoolscmdb.utils.YmlPropertyResourceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;
import tech.powerjob.worker.common.constants.StoreStrategy;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "powerjob.worker")
@PropertySource(value = {"classpath:powerjob.yml"}, ignoreResourceNotFound = false, encoding = "UTF-8", name = "powerjob.yml", factory = YmlPropertyResourceFactory.class)
@Validated
public class PowerJobProperties {
    private int akkaPort = 27777;

    @NotEmpty
    private String appName;

    @NotEmpty
    private String serverAddress;

    private StoreStrategy storeStrategy = StoreStrategy.DISK;

    @Min(value = 1)
    private int maxResultLength = 8192;

    @Min(value = 1)
    private int maxAppendedWfContextLength = 8192;

    private boolean enableTestMode = false;

    @NotEmpty
    private String password;
}
