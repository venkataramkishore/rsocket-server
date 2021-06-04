/**
 * 
 */
package com.kishore.learn.rsocket.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import com.kishore.rsocket.data.Message;

import reactor.core.publisher.Mono;

/**
 * @author kishore
 *
 */
@Controller
public class FireAndForgetMapping {

	
	private static final Logger log = LoggerFactory.getLogger(FireAndForgetMapping.class);

	@PreAuthorize("hasRole('USER')")
	@MessageMapping("fire-and-forget")
	public Mono<Void> fireAndLeave(Message msg, @AuthenticationPrincipal UserDetails user){
		log.info("Who is asking? '{}' with a role '{}'" ,user.getUsername(), user.getAuthorities());
		log.info("Inside fire and forget request...");
		log.info("Message received.. {}", msg);
		return Mono.empty();
	}
}
