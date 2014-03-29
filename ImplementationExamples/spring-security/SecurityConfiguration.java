package no.nkk.judgedirectory.web.config;

import no.nkk.judgedirectory.web.security.WhydahAuthenticationProvider;
import no.nkk.judgedirectory.web.security.WhydahEntryPoint;
import no.nkk.judgedirectory.web.security.WhydahService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Configuration for spring security. This is a two part configuration where the specialized
 * security configuration for spring which is not yet possible to set up in pure Java configuration
 * is set up in security.xml in the classpath. The dependent beans that are possible to configure
 * with Java is configured in this class.
 * 
 * @author Gunnar Skjold
 * 
 */
@Configuration
@ImportResource({ "classpath:security.xml" })
public class SecurityConfiguration {

	@Bean
	public WhydahEntryPoint whydahEntryPoint() {
		return new WhydahEntryPoint();
	}

	@Bean
	public WhydahAuthenticationProvider whydahAuthenticationProvider() {
		return new WhydahAuthenticationProvider();
	}

	@Bean
	public WhydahService whydahService() {
		return new WhydahService();
	}
}
