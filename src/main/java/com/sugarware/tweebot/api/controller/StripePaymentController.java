package com.sugarware.tweebot.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.sugarware.tweebot.api.entity.Subscription;
import com.sugarware.tweebot.api.repository.SubscriptionRepository;

@RestController
@RequestMapping("tweebot/payments")
public class StripePaymentController {

	@Autowired
	private Environment env;

	@Autowired
	private SubscriptionRepository subRepo;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> createSubscribtion(@RequestHeader HttpHeaders requestHeaders,
			@RequestParam Map<String, String> params, @RequestBody String body) {
		long userId = Long.parseLong(requestHeaders.get("userId").get(0));
		Gson gson = new Gson();
		HashMap<String, String> bdy = gson.fromJson(body, new HashMap<String, String>().getClass());
		System.out.println(body);

		String stripeToken = null;
		String stripeEmail = null;
		if (!bdy.containsKey("id") || !bdy.containsKey("email")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing parameters.");
		}

		stripeEmail = bdy.get("email");
		stripeToken = bdy.get("id");

		Stripe.apiKey = env.getProperty("stripe.secretKey");

		try {
			Map<String, Object> chargeParams = new HashMap<String, Object>();
			chargeParams.put("amount", 500); // Amount in cents
			chargeParams.put("currency", "usd");
			chargeParams.put("source", stripeToken);
			chargeParams.put("description", "tweebot tier 1");

			Charge charge = Charge.create(chargeParams);

			Subscription sub = subRepo.findByUserId(userId);
			sub.setSubscription(1);
			subRepo.save(sub);

		} catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error.");
		} catch (CardException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Card declined.");
		}

		return ResponseEntity.ok("ok");

	}

}
