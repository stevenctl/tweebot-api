package com.sugarware.tweebot.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.sugarware.tweebot.api.entity.AccessToken;
import com.sugarware.tweebot.api.entity.Subscription;
import com.sugarware.tweebot.api.repository.AccessTokenRepository;
import com.sugarware.tweebot.api.repository.SubscriptionRepository;
import com.sugarware.tweebot.api.service.TwitterUserService;
import com.sugarware.tweebot.api.util.ParamMapper;
import com.sugarware.tweebot.api.util.RequestSigner;

@RestController
@RequestMapping("/tweebot/connect/twitter")
public class TwitterUserController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RequestSigner requestSigner;

	@Autowired
	private AccessTokenRepository accessTokenRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private TwitterUserService twitterUserService;

	@Autowired
	private Environment env;
	
	@RequestMapping(value = "/postAuth", method = RequestMethod.GET)
	public ResponseEntity<?> redirectWithCookies(@RequestParam String oauth_token,
			@RequestParam String oauth_verifier) {

		String url = "https://api.twitter.com/oauth/access_token";
		Map<String, String> params = new HashMap<>();
		params.put("oauth_verifier", oauth_verifier);
		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.POST, params, oauth_token, null);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		String[] pairs = response.getBody().split("&");
		String new_oauth_token = pairs[0].split("=")[1];
		String oauth_token_secret = pairs[1].split("=")[1];

		long userId = Long.parseLong(new_oauth_token.split("-")[0]);
		AccessToken token = new AccessToken(userId, new_oauth_token, oauth_token_secret);
		accessTokenRepository.save(token);

		Subscription subscription = new Subscription(userId, 3);
		try {
			subscriptionRepository.save(subscription);
		} catch (Exception e) {
			// doesn't matter
		}

		System.out.println("NEW ACC: " + new_oauth_token.equals(oauth_token));

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Location", env.getProperty("homepageurl"));
		responseHeaders.add(HttpHeaders.SET_COOKIE, "oauth_token=" + new_oauth_token + "; PATH=/");
		responseHeaders.add(HttpHeaders.SET_COOKIE, "userId=" + userId + "; PATH=/");
		return new ResponseEntity<byte[]>(null, responseHeaders, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getAuthUrl() {
		String url = "https://api.twitter.com/oauth/request_token";
		Map<String, String> params = new HashMap<>();
		params.put("oauth_callback", env.getProperty("callbackurl"));
		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.POST, params, null, null);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);

		System.out.println("AUTH HEADER: " + authHeader);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		Map<String, String> responseValues = ParamMapper.paramsToMap(response.getBody());
		String oauth_token = responseValues.get("oauth_token");

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Location", "https://api.twitter.com/oauth/authenticate?oauth_token=" + oauth_token);
		return new ResponseEntity<byte[]>(null, responseHeaders, HttpStatus.FOUND);
	}

	@RequestMapping(method = RequestMethod.GET, value = "userInfo")
	public ResponseEntity<?> getUserInfo(@RequestHeader HttpHeaders requestHeaders) {
		String oauth_token = requestHeaders.get("oauth_token").get(0);
		AccessToken accessToken = accessTokenRepository.findByOauthToken(oauth_token);

		ResponseEntity<String> response = twitterUserService.getUserInfo(accessToken.getOauth_token(),
				accessToken.getOauth_token_secret());

		return response;
	}

}
