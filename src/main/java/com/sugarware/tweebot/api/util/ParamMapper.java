package com.sugarware.tweebot.api.util;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ParamMapper {

	private static final String ENC = "UTF-8";

	public static Map<String, String> paramsToMap(String paramString) {
		Map<String, String> params = new HashMap<>();
		String[] pairs = paramString.split("&");
		for (String pair : pairs) {
			String[] kv = pair.split("=");
			params.put(kv[0], kv[1]);
		}
		return params;
	}

	public static String mapToParams(Map<String, String> params) {
		Object[] keys = params.keySet().toArray(new Object[params.size()]);
		Arrays.sort(keys);
		String out = "";
		for (Object k : keys) {
			out += encode(k.toString(), ENC) + "=" + encode(params.get(k).toString(), ENC)
					+ (k.equals(keys[keys.length - 1]) ? "" : "&");
		}
		return out;
	}

	public static String mapToParamsUnencoded(Map<String, String> params) {
		Object[] keys = params.keySet().toArray(new Object[params.size()]);
		Arrays.sort(keys);
		String out = "";
		for (Object k : keys) {
			out += k.toString() + "=" + params.get(k).toString() + (k.equals(keys[keys.length - 1]) ? "" : "&");
		}
		return out;
	}

	public static String encode(String text, String encoding) {
		try {
			return URLEncoder.encode(text, encoding);
		} catch (Exception e) {
			return null;
		}

	}

}
