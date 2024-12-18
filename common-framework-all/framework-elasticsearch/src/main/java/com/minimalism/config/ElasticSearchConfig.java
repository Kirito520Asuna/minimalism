package com.minimalism.config;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.util.ObjectUtils;

/**
 * ES配置类：初始化ES客户端
 */
@Data
@Configuration
@EnableElasticsearchRepositories
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class ElasticSearchConfig {

//    spring:
//    elasticsearch:
//    rest:
//    uris: 192.168.200.128:9200
//    username: elastic
//    password: ppT8kmzu^3I

    private String host;
    private int port;
    private String uris;
    private String username;
    private String password;

    public String getHost() {
        String uris = getUris();
        if (ObjectUtil.isNotEmpty(uris)) {
            String[] split = uris.split(":");
            String host = split[0];
            return host;
        }
        return host;
    }

    public int getPort() {
        if (ObjectUtil.isNotEmpty(uris)) {
            String uris = getUris();
            String[] split = uris.split(":");
            String port = split[1];
            return Integer.parseInt(port);
        }
        return port;
    }

    @Bean
    public RestHighLevelClient client() {
        RestHighLevelClient rest;
        String defHost = "127.0.0.1";
        int defPort = 9200;
        String host = getHost();
        int port = getPort();

        host = ObjectUtils.isEmpty(host) ? defHost : host;
        port = ObjectUtils.isEmpty(port) ? defPort : port;

        if (ObjectUtils.isEmpty(username) && ObjectUtils.isEmpty(password)) {
            //无账号密码
            ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                    .connectedTo(
                            new StringBuilder(host)
                                    .append(":")
                                    .append(port)
                                    .toString())
                    .build();
            rest = RestClients.create(clientConfiguration).rest();
        } else {
            //有账号密码
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

            HttpHost httpHost = new HttpHost(host, port);
            RestClientBuilder builder = RestClient.builder(httpHost);
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

            rest = new RestHighLevelClient(builder);
        }
        return rest;
    }


}
