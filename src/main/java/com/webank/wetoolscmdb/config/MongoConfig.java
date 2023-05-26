package com.webank.wetoolscmdb.config;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.util.StringUtils;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

// mongodb client配置类
@Configuration("MongoConfig")  //等价于XML中配置bean
public class MongoConfig {
    @Autowired
    MongoIdToStringConverter mongoIdToStringConverter;

    /**
     * 此Bean也是可以不显示定义的，如果我们没有显示定义生成MongoTemplate实例，
     * SpringBoot利用我们配置好的MongoDbFactory在配置类中生成一个MongoTemplate，
     * 之后我们就可以在项目代码中直接@Autowired了。因为用于生成MongoTemplate
     * 的MongoDbFactory是我们自己在MongoConfig配置类中生成的，所以我们自定义的连接池参数也就生效了。
     *
     * @param mongoDatabaseFactory mongo工厂
     * @param converter      转换器
     * @return MongoTemplate实例
     */
    @Bean("mongoTemplate")
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory, MappingMongoConverter converter) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory, converter);
        // 设置读从库优先
//        mongoTemplate.setReadPreference(ReadPreference.secondaryPreferred());
        return mongoTemplate;
    }

    /**
     * 转换器
     * MappingMongoConverter可以自定义mongo转换器，主要自定义存取mongo数据时的一些操作，例如 mappingConverter.setTypeMapper(new
     * DefaultMongoTypeMapper(null)) 方法会将mongo数据中的_class字段去掉。
     *
     * @param factory     mongo工厂
     * @param context     上下文
     * @param conversions 自定义转换器
     * @return 转换器对象
     */
    @Bean("mappingMongoConverter")
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context, BeanFactory beanFactory,
                                                       @Qualifier("mongoCusConversions") CustomConversions conversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setCustomConversions(conversions);
        // remove _class field
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }
    @Bean(name = "mongoCusConversions")
    @Primary
    public CustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(mongoIdToStringConverter));
    }

    /**
     * 自定义mongo连接池
     *
     * @param properties 属性配置类
     * @return MongoDbFactory对象
     */
    @Bean("mongoDbFactory")
    public MongoDatabaseFactory mongoDbFactory(MongoProperties properties) {
        // 创建客户端参数
        MongoClientSettings mongoClientOptions = mongoClientOptions(properties);

        // 创建客户端
        MongoClient mongoClient = MongoClients.create(mongoClientOptions);

        return new SimpleMongoClientDatabaseFactory(mongoClient, properties.getDatabase());
    }

    /**
     * 创建认证
     *
     * @param properties 属性配置类
     * @return 认证对象
     */
    private MongoCredential getCredential(MongoProperties properties) {
        if (StringUtils.hasText(properties.getUsername()) && StringUtils.hasText(properties.getPassword())) {
            // 没有专用认证数据库则取当前数据库
            String database = StringUtils.hasText(properties.getAuthenticationDatabase()) ? properties.getAuthenticationDatabase() : properties.getDatabase();
            return MongoCredential.createCredential(properties.getUsername(), database,
                    properties.getPassword().toCharArray());
        }
        return null;
    }

    /**
     * 获取数据库服务地址
     *
     * @param mongoAddress 地址字符串
     * @return 服务地址数组
     */
    private List<ServerAddress> getServerAddress(String mongoAddress) {
        String[] mongoAddressArray = mongoAddress.trim().split(",");
        List<ServerAddress> serverAddressList = new ArrayList<>(4);
        for (String address : mongoAddressArray) {
            String[] hostAndPort = address.split(":");
            serverAddressList.add(new ServerAddress(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
        }
        return serverAddressList;
    }

    /**
     * mongo客户端参数配置
     *
     * @param properties 属性配置类
     * @return mongo客户端参数配置对象
     */
    private MongoClientSettings mongoClientOptions(MongoProperties properties) {
        MongoClientSettings.Builder settings = MongoClientSettings.builder();

        // 解析获取mongo服务地址
        List<ServerAddress> serverAddressList = getServerAddress(properties.getAddress());
        settings.applyToClusterSettings(builder -> builder.hosts(serverAddressList));

        // 创建认证
        MongoCredential mongoCredential = getCredential(properties);
        if(mongoCredential != null) {
            settings.credential(mongoCredential);
        }

        // socket连接配置
        settings.applyToSocketSettings(builder ->
                builder.connectTimeout(properties.getConnectionTimeoutMs(), MILLISECONDS)
                        .readTimeout(properties.getReadTimeoutMs(), MILLISECONDS));

        // 连接池配置
        settings.applyToConnectionPoolSettings(builder ->
                builder.maxConnectionIdleTime(properties.getMaxConnectionIdleTimeMs(), MILLISECONDS)
                        .maxConnectionLifeTime(properties.getMaxConnectionLifeTimeMs(), MILLISECONDS)
                        .maxWaitTime(properties.getMaxWaitTimeMs(), MILLISECONDS)
                        .maxSize(properties.getConnectionsMaxSize())
                        .minSize(properties.getConnectionsMinSize()));

        // server配置
        settings.applyToServerSettings(builder ->
                builder.heartbeatFrequency(properties.getHeartbeatFrequencyMs(), MILLISECONDS)
                        .minHeartbeatFrequency(properties.getMinHeartbeatFrequencyMs(), MILLISECONDS));

        // read preference
        // Operations read only from the secondary members of the set.
//        settings.readPreference(ReadPreference.secondaryPreferred());

        return settings.build();
    }
}



