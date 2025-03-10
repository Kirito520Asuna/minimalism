package com.minimalism.service.impl;

import com.minimalism.service.ElasticsearchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author yan
 * @Date 2024/7/26 0026 16:35:27
 * @Description
 */
@Service
@ConditionalOnBean(ElasticsearchRestTemplate.class)
public class ElasticsearchServiceImpl implements ElasticsearchService {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public ElasticsearchRestTemplate getElasticsearchRestTemplate() {
        return elasticsearchRestTemplate;
    }
}
