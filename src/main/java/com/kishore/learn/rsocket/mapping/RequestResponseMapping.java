/**
 * 
 */
package com.kishore.learn.rsocket.mapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.kishore.learn.rsocket.RsocketConceptsApplication;
import com.kishore.rsocket.data.Message;

import reactor.core.publisher.Mono;

/**
 * @author kishore
 *
 */
@Controller
public class RequestResponseMapping {


	private static final Logger logger = LogManager.getLogger(RsocketConceptsApplication.class);
	
	
	@MessageMapping("request-response")
	public Mono<Message> pingMessage(Message msg) throws InterruptedException {
		logger.info("Received request -message : {} ", msg);
		return Mono.just(new Message("You have said.."+ msg.getMessage()));
	}
}
