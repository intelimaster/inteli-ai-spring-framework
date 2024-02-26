package org.springframework.ai.autoconfigure.huggingface;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(HuggingfaceChatProperties.CONFIG_PREFIX)
public class HuggingfaceChatProperties {

	public static final String CONFIG_PREFIX = "spring.ai.huggingface.chat";

	private String apiKey;

	private String url;

	/**
	 * Enable Huggingface chat client.
	 */
	private boolean enabled = true;

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
