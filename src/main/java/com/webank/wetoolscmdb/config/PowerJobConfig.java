package com.webank.wetoolscmdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.utils.CommonUtils;
import tech.powerjob.common.utils.NetUtils;
import tech.powerjob.worker.PowerJobWorker;
import tech.powerjob.worker.common.PowerJobWorkerConfig;

import java.util.Arrays;
import java.util.List;

@Configuration("PowerJobConfig")
public class PowerJobConfig {
    @Bean
    public PowerJobWorker initPowerJob(PowerJobProperties properties) {
        /*
         * Address of PowerJob-server node(s). Do not mistake for ActorSystem port. Do not add
         * any prefix, i.e. http://.
         */
        List<String> serverAddress = Arrays.asList(properties.getServerAddress().split(","));

        /*
         * Create OhMyConfig object for setting properties.
         */
        PowerJobWorkerConfig config = new PowerJobWorkerConfig();
        /*
         * Configuration of worker port. Random port is enabled when port is set with non-positive number.
         */
        int port = properties.getAkkaPort();
        if (port <= 0) {
            port = NetUtils.getRandomPort();
        }
        config.setPort(port);
        /*
         * appName, name of the application. Applications should be registered in advance to prevent
         * error. This property should be the same with what you entered for appName when getting
         * registered.
         */
        config.setAppName(properties.getAppName());
        config.setServerAddress(serverAddress);
        /*
         * For non-Map/MapReduce tasks, {@code memory} is recommended for speeding up calculation.
         * Map/MapReduce tasks may produce batches of subtasks, which could lead to OutOfMemory
         * exception or error, {@code disk} should be applied.
         */
        config.setStoreStrategy(properties.getStoreStrategy());
        /*
         * When enabledTestMode is set as true, PowerJob-worker no longer connects to PowerJob-server
         * or validate appName.
         */
        config.setEnableTestMode(properties.isEnableTestMode());
        /*
         * Max length of appended workflow context . Appended workflow context value that is longer than the value will be ignore.
         */
        config.setMaxAppendedWfContextLength(properties.getMaxAppendedWfContextLength());
        /*
         * Create OhMyWorker object and set properties.
         */
        PowerJobWorker ohMyWorker = new PowerJobWorker();
        ohMyWorker.setConfig(config);
        return ohMyWorker;
    }

    @Bean
    public PowerJobClient intPowerJobClient(PowerJobProperties properties) {
        List<String> serverAddress = Arrays.asList(properties.getServerAddress().split(","));
        // 初始化 client，需要server地址和应用名称作为参数
        return new PowerJobClient(serverAddress, properties.getAppName(), properties.getPassword());
    }
}
