/*
 * Copyright 2023 - 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.ai.autoconfigure.vectorstore.elasticsearch;

import org.elasticsearch.client.RestClient;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.ElasticsearchVectorStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * @author Eddú Meléndez
 * @since 1.0.0
 */
@AutoConfiguration(after = ElasticsearchRestClientAutoConfiguration.class)
@ConditionalOnClass({ ElasticsearchVectorStore.class, EmbeddingClient.class, RestClient.class })
@EnableConfigurationProperties(ElasticsearchVectorStoreProperties.class)
class ElasticsearchVectorStoreAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	ElasticsearchVectorStore vectorStore(ElasticsearchVectorStoreProperties properties, RestClient restClient,
			EmbeddingClient embeddingClient) {
		if (StringUtils.hasText(properties.getIndexName())) {
			return new ElasticsearchVectorStore(properties.getIndexName(), restClient, embeddingClient);
		}
		return new ElasticsearchVectorStore(restClient, embeddingClient);
	}

}
