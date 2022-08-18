package com.webank.wetoolscmdb.config;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration("RestTemplateConfig")
public class RestTemplateConfig {
    public HttpClientConnectionManager poolingConnectionManager(RestTemplateProperties properties) {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        // 连接池最大连接数
        poolingConnectionManager.setMaxTotal(properties.getConnectionsMaxTotal());
        // 每个主机的并发
        poolingConnectionManager.setDefaultMaxPerRoute(properties.getConnectionsMaxPerRoute());
        // 空闲连接过期时间，ms
        poolingConnectionManager.setValidateAfterInactivity(properties.getConnectionsValidateAfterInactivity());
        return poolingConnectionManager;
    }

    public HttpClientBuilder httpClientBuilder(RestTemplateProperties properties) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // 设置HTTP连接管理器，连接池
        httpClientBuilder.setConnectionManager(poolingConnectionManager(properties));
        return httpClientBuilder;
    }

    @Bean("restTemplate")
    public RestTemplate restTemplate(RestTemplateProperties properties){
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setHttpClient(httpClientBuilder(properties).build());
        // 建立连接的超时时间，ms
        httpRequestFactory.setConnectionRequestTimeout(properties.getConnectionRequestTimeout());
        // 指客户端和服务器建立连接的timeout，ms
        httpRequestFactory.setConnectTimeout(properties.getConnectTimeout());
        // Socket的读超时时间，ms
        httpRequestFactory.setReadTimeout(properties.getReadTimeout());

        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        // HttpMessageConverter: 字符串到java对象的转化
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        // 支持 text/json 格式的 response json解析
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "json", StandardCharsets.UTF_8)));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);


        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter2 = new MappingJackson2HttpMessageConverter();
        // 支持 text/json 格式的 response json解析
        mappingJackson2HttpMessageConverter2.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "html", StandardCharsets.UTF_8)));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter2);

        return restTemplate;
    }
}
