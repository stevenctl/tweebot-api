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
import com.sugarware.tweebot.api.entity.FollowPolicy;
import com.sugarware.tweebot.api.repository.AccessTokenRepository;
import com.sugarware.tweebot.api.repository.FollowPolicyRepository;
import com.sugarware.tweebot.api.util.ValidationService;

@RestController
@RequestMapping("tweebot/policies/follow")
public class FollowPolicyController {

	@Autowired
	ValidationService validationService;

	@Autowired
	AccessTokenRepository accessTokenRepository;

	@Autowired
	FollowPolicyRepository followPolicyRepository;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> addFollowPolicy(@RequestHeader HttpHeaders requestHeaders, @RequestParam String hashtag) {
		long userId = Long.parseLong(requestHeaders.get("userId").get(0));
		String oauth_token = requestHeaders.get("oauth_token").get(0);
		AccessToken accessToken = accessTokenRepository.findByUserId(userId);
		String oauth_token_secret = accessToken.getOauth_token_secret();

		if (!validationService.doesUserMatchToken(userId, oauth_token, oauth_token_secret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Oauth tokens don't match user id.");
		}

		Iterable<FollowPolicy> userPolicies = followPolicyRepository.findByUserId(userId);
		for (FollowPolicy p : userPolicies) {
			if (p.getHashtag().equals(hashtag)) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("A policy exists for " + userId + " with hashtag " + hashtag);
			}
		}

		FollowPolicy policy = new FollowPolicy(-1, userId, hashtag, 0);
		followPolicyRepository.save(policy);

		return ResponseEntity.status(HttpStatus.CREATED).body("Policy created.");
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteFollowPolicy(@RequestHeader HttpHeaders requestHeaders, @RequestParam int policyId) {
		long userId = Long.parseLong(requestHeaders.get("userId").get(0));
		String oauth_token = requestHeaders.get("oauth_token").get(0);
		AccessToken accessToken = accessTokenRepository.findByUserId(userId);
		String oauth_token_secret = accessToken.getOauth_token_secret();

		if (!validationService.doesUserMatchToken(userId, oauth_token, oauth_token_secret)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Oauth tokens don't match user id.");
		}

		FollowPolicy policy = followPolicyRepository.findByPolicyId(policyId);

		if (policy == null) {
			return ResponseEntity.notFound().build();
		}

		if (policy.getUserId() != userId) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not own policy");
		}

		try {
			followPolicyRepository.delete(policy);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		return ResponseEntity.ok("Policy deleted.");

	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getFollowPolicies(@RequestHeader HttpHeaders requestHeaders) {
		long userId = Long.parseLong(requestHeaders.get("userId").get(0));
		String oauth_token = requestHeaders.get("oauth_token").get(0);
		AccessToken accessToken = accessTokenRepository.findByUserId(userId);
		String oauth_token_secret = accessToken.getOauth_token_secret();

		if (!validationService.doesUserMatchToken(userId, oauth_token, oauth_token_secret)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Oauth tokens don't match user id.");
		}

		Iterable<FollowPolicy> policies = followPolicyRepository.findByUserId(userId);

		return ResponseEntity.ok(policies);
	}
}
