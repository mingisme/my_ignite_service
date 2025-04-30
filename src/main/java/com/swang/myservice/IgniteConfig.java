package com.swang.myservice;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.kubernetes.configuration.KubernetesConnectionConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IgniteConfig {

    @Bean
    public Ignite igniteInstance() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("spring-boot-ignite-node");

        // Configure discovery
        TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();

        //multicast
//        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
//        ipFinder.setAddresses(Arrays.asList("127.0.0.1:47500..47509"));

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
