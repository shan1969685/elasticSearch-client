package com.demo.mocc.eswl.eswl;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @program eswl
 * @description: m
 * @author: sr
 * @create: 2019/09/01 15:20
 */
@Configuration
public class MyConfig {

    @Bean
    public TransportClient client ()throws  Exception{
        Settings settings = Settings.builder().put("cluster.name","xiaoming")
                .put("client.transport.sniff", "true")     //client.transport.sniff参数，集群群嗅探机制。
                // 在创建TransportClient时可以通过addTransportAddress来静态的增加ElasticSearch集群中的节点，
                // 如果开启集群群嗅探机制，即开启节点动态发现机制，允许动态添加和删除节点。当启用嗅探功能时，
                // 首先客户端会连接addTransportAddress中的节点上。在此之后，客户端将调用这些节点上的内部集群状态API来发现可用的数据节点。

                .build();
        TransportClient client = new PreBuiltTransportClient(settings);
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),8200));
        client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),8000));
        return  client;
    }
}
