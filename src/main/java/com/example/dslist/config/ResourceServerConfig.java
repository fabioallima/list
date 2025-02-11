package com.example.dslist.config;

import com.example.dslist.config.customgrant.CustomAccessDeniedHandler;
import com.example.dslist.config.customgrant.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

	@Value("${cors.origins}")
	private String corsOrigins;

	@Bean
	@Profile({"dev", "test"})
	@Order(1)
	SecurityFilterChain h2SecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.securityMatcher(PathRequest.toH2Console())
				// Desabilita CSRF para o console H2 em ambientes de dev/test
				// Isso é necessário para o funcionamento correto do console H2
				.csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
				.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(PathRequest.toH2Console())
						.access((authentication, context) -> {
							IpAddressMatcher localhost = new IpAddressMatcher("127.0.0.1");
							return new AuthorizationDecision(localhost.matches(context.getRequest()));
						})
				);
		return http.build();
	}

	@Bean
	@Order(3)
	SecurityFilterChain rsSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
						.anyRequest().permitAll()
				)
				.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()))
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.addFilterBefore(new CorsFilter(corsConfigurationSource()), CorsFilter.class)
				.exceptionHandling(exh -> exh
						.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
						.accessDeniedHandler(new CustomAccessDeniedHandler())
				);

		return http.build();
	}


	@Bean
	JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
		grantedAuthoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {

		String[] origins = corsOrigins.split(",");

		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowedOriginPatterns(Arrays.asList(origins));
		corsConfig.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH"));
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", corsConfig);
		return source;
	}

	/*
	 * @Bean FilterRegistrationBean<CorsFilter> corsFilter() {
	 * FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>( new
	 * CorsFilter(corsConfigurationSource()));
	 * bean.setOrder(Ordered.HIGHEST_PRECEDENCE); return bean; }
	 */
}
