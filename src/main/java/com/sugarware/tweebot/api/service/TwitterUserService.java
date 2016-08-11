package com.sugarware.tweebot.api.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sugarware.tweebot.api.util.ParamMapper;
import com.sugarware.tweebot.api.util.RequestSigner;

@Service
public class TwitterUserService {

	@Autowired
	private RequestSigner requestSigner;

	@Autowired
	private RestTemplate restTemplate;

	public ResponseEntity<String> getUserInfo(String oauth_token, String oauth_token_secret) {
		String url = "https://api.twitter.com/1.1/account/verify_credentials.json";

		Map<String, String> params = new HashMap<>();

		params.put("skip_status", "true");
		params.put("include_entities", "false");

		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.GET, params, oauth_token,
				oauth_token_secret);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);

		return restTemplate.exchange(url + "?" + ParamMapper.mapToParams(params), HttpMethod.GET, request,
				String.class);

	}

}
