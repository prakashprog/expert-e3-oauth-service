package com.expertworks.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.filter.CorsFilter;

import com.expertworks.model.ExpertUser;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(AuthServerConfig.class);


    private static final int ACCESS_TOKEN_VALIDITY_SECONDS =  20;
	//private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 60 * 60;
	//private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 4 * 60;
	private static final int REFRESH_TOKEN_VALIDITY_SECONDS = 5 *60 * 60;

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Autowired
	private AuthenticationManager authenticationManager;

//	@Bean
//	public TokenStore tokenStore() {
//		return new JwtTokenStore(jwtAccessTokenConverter());
//	}

	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter() {

			@Override
			public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
				// if
				// (authentication.getOAuth2Request().getGrantType().equalsIgnoreCase("password"))
				// {
				final Map<String, Object> additionalInfo = new HashMap<String, Object>();
				additionalInfo.put("userId", authentication.getName());
				additionalInfo.put("name", ((ExpertUser) authentication.getPrincipal()).getFirstName());
				additionalInfo.put("teamId", ((ExpertUser) authentication.getPrincipal()).getTeamId());
				additionalInfo.put("role", ((ExpertUser) authentication.getPrincipal()).getRole());
				additionalInfo.put("logo", ((ExpertUser) authentication.getPrincipal()).getLogo());
				additionalInfo.put("partnerId", ((ExpertUser) authentication.getPrincipal()).getPartnerId());
				additionalInfo.put("groupId", ((ExpertUser) authentication.getPrincipal()).getGroupId());


				((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

				logger.info("cred : " + (authentication.getPrincipal()));
				// System.out.println("cred : " + ((ExpertUser)
				// authentication.getPrincipal()).getFirstName());
				// }
				accessToken = super.enhance(accessToken, authentication);
				// ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(new
				// HashMap<>());
				return accessToken;
			}
		};

		converter.setSigningKey(jwtSecret);
		return converter;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		/*
		 * clients.inMemory().withClient("client").secret("secret")
		 * .authorizedGrantTypes("password", "authorization_token", "refresh_token",
		 * "implicit").scopes("write")
		 * .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
		 * .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
		 */

		clients.inMemory().withClient("client").secret("secret")
		.authorizedGrantTypes("password", "authorization_token", "refresh_token", "implicit","authorization_code").scopes("write")
		.accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
		.refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);

	}

	//https://coderedirect.com/questions/337694/revoke-jwt-oauth2-refresh-token
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//		endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore())
//				.accessTokenConverter(jwtAccessTokenConverter()).userDetailsService(userDetailsService);

		endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore()).reuseRefreshTokens(false)
		.accessTokenConverter(jwtAccessTokenConverter()).userDetailsService(userDetailsService);

	}

	@Bean
	public FilterRegistrationBean customCorsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));

		// IMPORTANT #2: I didn't stress enough the importance of this line in my
		// original answer,
		// but it's here where we tell Spring to load this filter at the right point in
		// the chain
		// (with an order of precedence higher than oauth2's filters)
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
		//oauthServer.allowFormAuthenticationForClients().checkTokenAccess("permitAll()");
	}

	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		System.out.println("inside logging filter");
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(true);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
		loggingFilter.setIncludeHeaders(false);
		loggingFilter.setMaxPayloadLength(64000);
		loggingFilter.setBeforeMessagePrefix("---REQUEST DATA : ");
		loggingFilter.setAfterMessagePrefix("---RESPONSE DATA : ");
		return loggingFilter;
	}

}
