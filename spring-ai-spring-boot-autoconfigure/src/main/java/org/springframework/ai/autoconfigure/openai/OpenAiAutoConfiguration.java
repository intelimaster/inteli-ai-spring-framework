/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.autoconfigure.openai;

import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import org.springframework.ai.autoconfigure.NativeHints;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.client.OpenAiClient;
import org.springframework.ai.openai.embedding.OpenAiEmbeddingClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.util.StringUtils;

import static org.springframework.ai.autoconfigure.openai.OpenAiProperties.CONFIG_PREFIX;

@AutoConfiguration
@ConditionalOnClass(OpenAiService.class)
@EnableConfigurationProperties(OpenAiProperties.class)
@ImportRuntimeHints(NativeHints.class)
public class OpenAiAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public OpenAiClient openAiClient(OpenAiProperties openAiProperties) {
		OpenAiClient openAiClient = new OpenAiClient(theoOpenAiService(openAiProperties.getBaseUrl(),
				openAiProperties.getApiKey(), openAiProperties.getDuration()));
		openAiClient.setTemperature(openAiProperties.getTemperature());
		openAiClient.setModel(openAiProperties.getModel());
		return openAiClient;
	}

	@Bean
	@ConditionalOnMissingBean
	public EmbeddingClient openAiEmbeddingClient(OpenAiProperties openAiProperties) {
		return new OpenAiEmbeddingClient(theoOpenAiService(openAiProperties.getEmbeddingBaseUrl(),
				openAiProperties.getEmbeddingApiKey(), openAiProperties.getDuration()),
				openAiProperties.getEmbeddingModel());
	}

	private OpenAiService theoOpenAiService(String baseUrl, String apiKey, Duration duration) {

		if ("https://api.openai.com".equals(baseUrl) && !StringUtils.hasText(apiKey)) {
			throw new IllegalArgumentException(
					"You must provide an API key with the property name " + CONFIG_PREFIX + ".api-key");
		}

		ObjectMapper mapper = OpenAiService.defaultObjectMapper();
		OkHttpClient client = OpenAiService.defaultClient(apiKey, duration);

		// Waiting for https://github.com/TheoKanning/openai-java/issues/249 to be
		// resolved.
		Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
			.client(client)
			.addConverterFactory(JacksonConverterFactory.create(mapper))
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.build();

		OpenAiApi api = retrofit.create(OpenAiApi.class);

		return new OpenAiService(api);
	}

}
