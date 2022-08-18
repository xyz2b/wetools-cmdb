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

@Getter
@Setter
@ToString
@Configuration("RestTemplateProperties")
@ConfigurationProperties(prefix = "http.client")
@PropertySource(value = {"classpath:http-client.yml"}, ignoreResourceNotFound = false, encoding = "UTF-8", name = "http-client.yml", factory = YmlPropertyResourceFactory.class)
@Validated
public class RestTemplateProperties {
    /**
     * 基础连接参数
     */
    @Min(value = 1)
    private int connectionRequestTimeout;   // 建立连接的超时时间，ms
    private int connectTimeout;             // 客户端和服务器建立连接的timeout，ms
    private int readTimeout;                // Socket的读超时时间，ms


    /**
     * 客户端连接池参数
     */
    @Min(value = 1)
    private int connectionsMaxTotal;        // 连接池最大连接数
    @Min(value = 1)
    private int connectionsMaxPerRoute;     // 每个主机的并发
    @Min(value = 1)
    private int connectionsValidateAfterInactivity;  // 空闲连接过期时间，ms
}
