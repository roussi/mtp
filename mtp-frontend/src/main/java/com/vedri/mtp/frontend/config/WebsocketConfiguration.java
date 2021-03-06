package com.vedri.mtp.frontend.config;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.vedri.mtp.frontend.transaction.aggregation.subscription.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import com.vedri.mtp.frontend.MtpFrontendConstants;
import com.vedri.mtp.frontend.web.security.AuthoritiesConstants;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

	public static final String IP_ADDRESS = "IP_ADDRESS";

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker(MtpFrontendConstants.TOPIC_DESTINATION_PREFIX);
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint(
				MtpFrontendConstants.wrapWebsocketPath("mtp"))
				.setHandshakeHandler(new DefaultHandshakeHandler() {
					@Override
					protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
							Map<String, Object> attributes) {
						Principal principal = request.getPrincipal();
						if (principal == null) {
							Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
							authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
							principal = new AnonymousAuthenticationToken("WebsocketConfiguration", "anonymous",
									authorities);
						}
						return principal;
					}
				})
				.withSockJS()
				.setInterceptors(httpSessionHandshakeInterceptor());
	}

	@Bean
	public HandshakeInterceptor httpSessionHandshakeInterceptor() {
		return new HandshakeInterceptor() {

			@Override
			public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
				if (request instanceof ServletServerHttpRequest) {
					ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
					attributes.put(IP_ADDRESS, servletRequest.getRemoteAddress());
				}
				return true;
			}

			@Override
			public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
					WebSocketHandler wsHandler, Exception exception) {

			}
		};
	}
}
