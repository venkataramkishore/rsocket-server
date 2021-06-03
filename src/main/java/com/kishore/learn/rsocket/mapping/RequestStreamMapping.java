/**
 * 
 */
package com.kishore.learn.rsocket.mapping;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.kishore.rsocket.data.Message;

import reactor.core.publisher.Flux;

/**
 * @author kishore
 *
 */
@Controller
public class RequestStreamMapping {

	
	private static final Logger logger = LoggerFactory.getLogger(RequestStreamMapping.class);

	@MessageMapping("request-stream")
	public Flux<Message> streamOfMessages(final Message msg) throws InterruptedException {
		logger.info("Inside stream of messages with received  message {}", msg);
		return Flux
				.interval(Duration.ofSeconds(1))
				.map(index -> new Message("Message interval "+ index.toString()))
				.log();
	}
}
