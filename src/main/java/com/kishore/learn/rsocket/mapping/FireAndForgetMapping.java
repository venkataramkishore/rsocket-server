/**
 * 
 */
package com.kishore.learn.rsocket.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

	@MessageMapping("fire-and-forget")
	public Mono<Void> fireAndLeave(Message msg){
		log.info("Inside fire and forget request...");
		log.info("Message received.. {}", msg);
		return Mono.empty();
	}
}
