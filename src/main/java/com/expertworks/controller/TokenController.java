package com.expertworks.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

	private final static Logger logger = LoggerFactory.getLogger(TokenController.class);

	@CrossOrigin
	@GetMapping
	@RequestMapping({ "/" })
	public String firstPage() {
		return "Auth Service Running...";
	}

	@Autowired
	private TokenStore tokenStore;

	@Bean
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore);
		defaultTokenServices.setSupportRefreshToken(true);
		return defaultTokenServices;
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/tokens/{userName}")
	@ResponseBody
	public List<String> getTokens(@PathVariable("userName") String userName) {
		List<String> tokenValues = new ArrayList<String>();
		// Collection<OAuth2AccessToken> tokens =
		// tokenStore.findTokensByClientId("client");
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName("client", userName);

		System.out.println(tokens);
		if (tokens != null) {
			System.out.println(tokens.size());
			for (OAuth2AccessToken token : tokens) {

				tokenValues.add(token.getValue());
			}
		}
		return tokenValues;
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/tokens/revoke/{tokenId:.*}")
	@ResponseBody
	public String revokeToken(@PathVariable String tokenId) {

		logger.info("revokeToken tokenId :  " + tokenId);
		tokenServices().revokeToken(tokenId);
		return tokenId;
	}

	@RequestMapping(method = RequestMethod.POST, value = "/tokens/revokeRefreshToken/{tokenId:.*}")
	@ResponseBody
	public String revokeRefreshToken(@PathVariable String tokenId) {
	    if (tokenStore instanceof InMemoryTokenStore){
	    	OAuth2RefreshToken oauth2RefreshToken=tokenStore.readRefreshToken(tokenId);
	    	System.out.println(" revokeRefreshToken : " + tokenId);
	         tokenStore.removeRefreshToken(oauth2RefreshToken);
	    }
	    return tokenId;
	}



}
