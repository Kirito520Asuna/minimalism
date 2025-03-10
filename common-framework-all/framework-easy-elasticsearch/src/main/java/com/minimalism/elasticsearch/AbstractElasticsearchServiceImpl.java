package com.minimalism.elasticsearch;

import com.minimalism.config.EasyElasticsearchConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Author yan
 * @Date 2024/8/13 0013 16:12:17
 * @Description
 */
@Service
@ConditionalOnBean(EasyElasticsearchConfig.class)
public class AbstractElasticsearchServiceImpl implements AbstractElasticsearchService{
}
