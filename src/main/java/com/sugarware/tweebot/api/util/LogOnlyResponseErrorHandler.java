package com.sugarware.tweebot.api.util;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class LogOnlyResponseErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		return !response.getStatusCode().is2xxSuccessful();
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		System.err.println(response.getRawStatusCode() + " " + response.getStatusText());
	}
	
}
