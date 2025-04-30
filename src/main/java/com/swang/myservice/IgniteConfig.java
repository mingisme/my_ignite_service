package com.swang.myservice;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.PartitionLossPolicy;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class IgniteConfig {

    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("spring-boot-ignite-node");

        CacheConfiguration cacheCfg = new CacheConfiguration("myCache");
        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setBackups(2);
        cacheCfg.setRebalanceMode(CacheRebalanceMode.SYNC);
        cacheCfg.setWriteSynchronizationMode(CacheWriteSynchronizationMode.FULL_SYNC);
        cacheCfg.setPartitionLossPolicy(PartitionLossPolicy.READ_ONLY_SAFE);
        cfg.setCacheConfiguration(cacheCfg);

        // Configure discovery
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

        //multicast
//        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
//        ipFinder.setAddresses(List.of("127.0.0.1:47500..47509"));

        //kubernetes
        String serviceName = System.getenv("IGNITE_K8S_SERVICE_NAME");
        String namespace = System.getenv("IGNITE_K8S_NAMESPACE");
        KubernetesConnectionConfiguration configuration = new KubernetesConnectionConfiguration();
        if (serviceName != null) {
            configuration.setServiceName(serviceName);
        }
        if (namespace != null) {
            configuration.setNamespace(namespace);
        }
        TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder(configuration);

        discoverySpi.setIpFinder(ipFinder);
        cfg.setDiscoverySpi(discoverySpi);

        cfg.setPeerClassLoadingEnabled(true);

        return Ignition.start(cfg);
    }
}
