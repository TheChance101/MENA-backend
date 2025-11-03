package net.thechance.dukan.search.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories(basePackages = ["net.thechance.dukan.repository"])
class DukanElasticConfig
