package com.sugarware.tweebot.api.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

@Service
public class RequestSigner {

	private static final String ENC = "UTF-8";

	@Autowired
	private Environment env;

	@Autowired
	private SecureRandom random;

	private String generateSignature(String signatueBaseStr, String oAuthConsumerSecret, String oAuthTokenSecret) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec spec;
			if (null == oAuthTokenSecret) {
				String signingKey = ParamMapper.encode(oAuthConsumerSecret, ENC) + '&';
				spec = new SecretKeySpec(signingKey.getBytes(), "HmacSHA1");
			} else {
				String signingKey = ParamMapper.encode(oAuthConsumerSecret, ENC) + '&'
						+ ParamMapper.encode(oAuthTokenSecret, ENC);
				spec = new SecretKeySpec(signingKey.getBytes(), "HmacSHA1");
			}
			mac.init(spec);
			byteHMAC = mac.doFinal(signatueBaseStr.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new BASE64Encoder().encode(byteHMAC);
	}

	public String getAuthorizationHeader(String url, HttpMethod method, Map<String, String> paramsMap,
			String oauthToken, String oauthSecret) {

		Map<String, String> params = new HashMap<>();
		for (String s : paramsMap.keySet())
			params.put(s, paramsMap.get(s));

		String oauth_nonce = new BigInteger(130, random).toString(32);
		String oauth_consumer_key = env.getProperty("twitter.consumerKey");
		String oauth_signature_method = "HMAC-SHA1";
		String oauth_timestamp = "" + (int) (System.currentTimeMillis() / 1000L);
		String oauth_token = oauthToken;
		String oauth_version = "1.0";

		params.put("oauth_nonce", oauth_nonce);
		params.put("oauth_consumer_key", oauth_consumer_key);
		params.put("oauth_signature_method", oauth_signature_method);
		params.put("oauth_timestamp", oauth_timestamp);
		if (oauthToken != null)
			params.put("oauth_token", oauth_token);
		params.put("oauth_version", oauth_version);

		String paramString = ParamMapper.mapToParams(params);
		System.out.println("PARAMSTRING:" + paramString);
		String baseString = getBaseSigningString(url, method, paramString);
		System.out.println("BASESTRING:" + baseString);
		String oauth_signature = generateSignature(baseString, env.getProperty("twitter.secretKey"), oauthSecret);
		System.out.println("SIGN:" + oauth_signature);
		params.put("oauth_signature", oauth_signature);

		StringBuilder authHeader = new StringBuilder();
		authHeader.append("OAuth ");

		String[] keys = params.keySet().toArray(new String[params.size()]);
		Arrays.sort(keys);

		for (String k : keys) {
			if (!k.startsWith("oauth_"))
				continue;
			authHeader.append(ParamMapper.encode(k, ENC));
			authHeader.append("=\"");
			authHeader.append(ParamMapper.encode(params.get(k), ENC));
			authHeader.append("\"");
			if (k != keys[keys.length - 1]) {
				authHeader.append(",");
			}
		}

		String authString = authHeader.toString();
		if (authString.endsWith(","))
			authString = authString.substring(0, authString.length() - 1);

		return authString;

	}

	public String getBaseSigningString(String url, HttpMethod method, String params) {
		/**
		 * base has three parts, they are connected by "&": 1) protocol 2) URL
		 * (need to be URLEncoded) 3) Parameter List (need to be URLEncoded).
		 */
		try {
			StringBuilder base = new StringBuilder();
			base.append(method.name());
			base.append("&");
			base.append(ParamMapper.encode(url, ENC));
			base.append("&");
			base.append(ParamMapper.encode(params, ENC));
			return base.toString();
		} catch (Exception e) {
			return null;
		}
	}

}
