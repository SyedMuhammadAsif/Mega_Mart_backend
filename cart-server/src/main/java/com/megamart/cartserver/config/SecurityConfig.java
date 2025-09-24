package com.megamart.cartserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// Disable CSRF for simplicity in initial development
		http.csrf(csrf -> csrf.disable());

		// Stateless session; we may add JWT later
		http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// Permit all endpoints for now, including H2 console
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers("/h2-console/**").permitAll()
			.anyRequest().permitAll()
		);

		// Allow frames for H2 console
		http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

		// HTTP Basic to have something enabled (not enforced due to permitAll)
		http.httpBasic(Customizer.withDefaults());

		return http.build();
	}
} 