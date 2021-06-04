/**
 * 
 */
package com.kishore.learn.rsocket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;

/**
 * @author kishore
 *
 */
@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class RSocketSecurityConfig {

	@Bean
	RSocketMessageHandler socketMessageHandler(RSocketStrategies rSocketStrategies) {
		RSocketMessageHandler messageHandler = new RSocketMessageHandler();
		messageHandler.getArgumentResolverConfigurer()
		.addCustomResolver(new AuthenticationPrincipalArgumentResolver());
		messageHandler.setRSocketStrategies(rSocketStrategies);
		
		return messageHandler;
	}
	
	@Bean
	MapReactiveUserDetailsService authentication() {
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

		UserDetails user = User.builder()
				.username("kishore")
				.password("password123")
				.roles("USER")
				.passwordEncoder(encoder::encode)
				.build();

		UserDetails admin = User.builder()
				.username("admin")
				.password("admin")
				.roles("ADMIN")
				.passwordEncoder(encoder::encode)
				.build();
		
		return new MapReactiveUserDetailsService(user, admin);
	}
	
	@Bean
	PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
		security.authorizePayload(authorize -> authorize.anyExchange().authenticated())
		.simpleAuthentication(Customizer.withDefaults());
		
		return security.build();
	}
	
}
