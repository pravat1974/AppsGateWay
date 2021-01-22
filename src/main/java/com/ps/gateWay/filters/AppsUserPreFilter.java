package com.ps.gateWay.filters;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AppsUserPreFilter implements GlobalFilter {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.info(" AppsUserPreFilter  is called..");
		ServerHttpRequest request = exchange.getRequest();
		String ipAddress = request.getRemoteAddress().getHostName();
		String requestPath = request.getPath().toString();
		HttpHeaders headers = request.getHeaders();
		headers.entrySet().forEach(keyHeader -> {
			log.info("Headeris " + headers.get(keyHeader));
		});

		if (!request.getHeaders().containsKey("Authorization")) {
			return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
		}
		;

		String authorizationHeader = request.getHeaders().get("Authorization").get(0);

		if (!this.isAuthorizationValid(authorizationHeader)) {
			return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
		}

		ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
				.header("secret", RandomStringUtils.random(10)).build();

		return chain.filter(exchange.mutate().request(modifiedRequest).build());

	}

	private boolean isAuthorizationValid(String authorizationHeader) {
		boolean isValid = true;

		// Logic needs to be added

		return isValid;
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);

		return response.setComplete();
	}

}
