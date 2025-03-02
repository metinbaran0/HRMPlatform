package org.hrmplatform.hrmplatform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	
	@Value("${app.base-url}")
	private String baseUrl;
	
	@Value("${hrmplatform.siteAdminEmail}")
	private String siteAdminEmail;
	
	public String getBaseUrl() {
		return baseUrl;
	}
	
	public String getSiteAdminEmail() {
		return siteAdminEmail;
	}
}