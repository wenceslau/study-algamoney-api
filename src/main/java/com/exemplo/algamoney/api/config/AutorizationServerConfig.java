package com.exemplo.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.exemplo.algamoney.api.config.token.CustomTokenEnhancer;

@Profile("oauth-security")
@Configuration
@EnableAuthorizationServer
public class AutorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;

	// Configuração da aplicação cliente, nao é o usuario
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

		System.out.println("configure 1");
		
		clients.inMemory()
			.withClient("angular")
			.secret("@ngul@r0")
			.scopes("read", "write", "delete")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(60 * 5)
			.refreshTokenValiditySeconds(3600 * 24)
		.and()
			.withClient("mobile")
			.secret("m0b1l30")
			.scopes("read")
			.authorizedGrantTypes("password", "refresh_token")
			.accessTokenValiditySeconds(60 * 5)
			.refreshTokenValiditySeconds(3600 * 24);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		System.out.println("configure 2");
		
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnchancer(), accessTokenConverter()));
		
		
		endpoints.tokenStore(tokenStore())
		.tokenEnhancer(tokenEnhancerChain)
		.reuseRefreshTokens(false)
		.authenticationManager(authenticationManager);

	}

	private TokenEnhancer tokenEnchancer() {
		
		System.out.println("TokenEnhancer");
		return new CustomTokenEnhancer();
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		System.out.println("JwtAccessTokenConverter");
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks");

		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore() {
		System.out.println("TokenStore");
		return new JwtTokenStore(accessTokenConverter());
	}

}
