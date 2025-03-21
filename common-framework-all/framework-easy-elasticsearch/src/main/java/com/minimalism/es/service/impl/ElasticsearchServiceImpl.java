package com.minimalism.es.service.impl;

import com.minimalism.es.config.EasyElasticsearchConfig;
import com.minimalism.es.service.ElasticsearchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2024/8/13 0013 16:12:17
 * @Description
 */
@Service
@ConditionalOnBean(EasyElasticsearchConfig.class)
public class ElasticsearchServiceImpl implements ElasticsearchService {
}
