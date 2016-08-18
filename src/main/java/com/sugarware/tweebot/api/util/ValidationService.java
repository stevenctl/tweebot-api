package com.sugarware.tweebot.api.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sugarware.tweebot.api.entity.AccessToken;
import com.sugarware.tweebot.api.repository.AccessTokenRepository;
import com.sugarware.tweebot.api.repository.LikePolicyRepository;

@Service
public class ValidationService {

	@Autowired
	AccessTokenRepository accessTokenRepository;

	@Autowired
	LikePolicyRepository likePolicyRepository;

	public boolean doesUserMatchToken(long userId, String oauth_token, String oauth_token_secret) {
		AccessToken token = accessTokenRepository.findByUserId(userId);

		if (token == null || !token.getOauth_token().equals(oauth_token)
				| !token.getOauth_token_secret().equals(oauth_token_secret)) {
			return false;
		}

		return true;
	}

	public boolean isAlphanumericWord(String s) {
		String pattern = "^[a-zA-Z0-9]*$";
		return s.matches(pattern);
	}
	
	public boolean isNumeric(String s){
		String pattern = "^[0-9]*$";
		return s.matches(pattern);
	}
}
