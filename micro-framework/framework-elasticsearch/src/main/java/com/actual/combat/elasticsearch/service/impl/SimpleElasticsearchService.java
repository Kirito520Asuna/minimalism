package com.actual.combat.elasticsearch.service.impl;

import com.actual.combat.elasticsearch.service.ElasticsearchService;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import javax.annotation.Resource;



/**
 * @Author yan
 * @Date 2024/7/26 0026 16:35:27
 * @Description
 */

public class SimpleElasticsearchService implements ElasticsearchService {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public ElasticsearchRestTemplate getElasticsearchRestTemplate() {
        return elasticsearchRestTemplate;
    }
}
