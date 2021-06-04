/**
 * 
 */
package com.kishore.learn.rsocket.mapping;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;

import com.kishore.rsocket.data.Message;

import reactor.core.publisher.Flux;
import org.springframework.stereotype.Controller;

/**
 * @author kishore
 *
 */
@Controller
public class Stream2StreamMapping {

	
	private static final Logger logger = LoggerFactory.getLogger(Stream2StreamMapping.class);

	@PreAuthorize("hasRole('USER')")
	@MessageMapping("stream-stream")
	public Flux<Message> channelStreams(final Flux<Duration> settings) {
		logger.info("Inside channel streams with settings {}", settings);
		return settings
				.doOnNext(setting -> logger.info("Requested interval is {} seconds", setting))
				.doOnCancel(() -> logger.warn("Client has requested to cancel the channel"))
				.switchMap(setting -> Flux.interval(setting))
						.map(index -> new Message("Stream response #" + index))
				.log();
	}
}
