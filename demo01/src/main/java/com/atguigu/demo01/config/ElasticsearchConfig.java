package com.atguigu.demo01.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/9 14:27
 * @Description:
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient(){

        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.25.128",9200,"http")
                        //集群下可以配置多个
                )
        );

    }

}
