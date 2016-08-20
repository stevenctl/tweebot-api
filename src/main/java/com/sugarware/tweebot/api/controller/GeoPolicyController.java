package com.sugarware.tweebot.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sugarware.tweebot.api.entity.AccessToken;
import com.sugarware.tweebot.api.entity.GeoPolicy;
import com.sugarware.tweebot.api.repository.AccessTokenRepository;
import com.sugarware.tweebot.api.repository.GeoPolicyRepository;
import com.sugarware.tweebot.api.util.ValidationService;

@RestController
@RequestMapping("tweebot/policies/geo")
public class GeoPolicyController {

	@Autowired
	private ValidationService validationService;

	@Autowired
	private AccessTokenRepository accessTokenRepository;

	@Autowired
	private GeoPolicyRepository geoPolicyRepository;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getGeoPolicy(@RequestHeader HttpHeaders requestHeaders) {
		long userId = Long.parseLong(requestHeaders.get("userId").get(0));
		String oauth_token = requestHeaders.get("oauth_token").get(0);
		AccessToken accessToken = accessTokenRepository.findByUserId(userId);
		String oauth_token_secret = accessToken.getOauth_token_secret();

		if (!validationService.doesUserMatchToken(userId, oauth_token, oauth_token_secret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Oauth tokens don't match user id.");
		}

		GeoPolicy policy = geoPolicyRepository.findByUserId(userId);
		if (policy == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This user has no geopolicy.");
		}

		return ResponseEntity.ok(policy);
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createGeoPolicy(@RequestHeader HttpHeaders requestHeaders, @RequestParam int zip,
			@RequestParam int radius) {
		long userId = Long.parseLong(requestHeaders.get("userId").get(0));
		String oauth_token = requestHeaders.get("oauth_token").get(0);
		AccessToken accessToken = accessTokenRepository.findByUserId(userId);
		String oauth_token_secret = accessToken.getOauth_token_secret();

		if (!validationService.doesUserMatchToken(userId, oauth_token, oauth_token_secret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Oauth tokens don't match user id.");
		}

		if (geoPolicyRepository.findByUserId(userId) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Geopolicy exists for " + userId);
		}

		GeoPolicy policy = new GeoPolicy(userId, zip, radius, true);
		try {
			geoPolicyRepository.save(policy);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving geopolicy.");
		}

		return ResponseEntity.status(HttpStatus.CREATED).body("Created geopolicy for user " + userId);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<?> updateGeoPolicy(@RequestHeader HttpHeaders requestHeaders, @RequestParam int zip,
			@RequestParam int radius, @RequestParam boolean active) {
		long userId = Long.parseLong(requestHeaders.get("userId").get(0));
		String oauth_token = requestHeaders.get("oauth_token").get(0);
		AccessToken accessToken = accessTokenRepository.findByUserId(userId);
		String oauth_token_secret = accessToken.getOauth_token_secret();

		if (!validationService.doesUserMatchToken(userId, oauth_token, oauth_token_secret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Oauth tokens don't match user id.");
		}

		GeoPolicy policy = geoPolicyRepository.findByUserId(userId);
		if (policy == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No geopolicy exists for " + userId);
		}

		policy.setRadius(radius);
		policy.setActive(active);
		policy.setZipCode(zip);

		try {
			geoPolicyRepository.save(policy);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving geopolicy.");
		}

		return ResponseEntity.ok("Created geopolicy for user " + userId);
	}

}
