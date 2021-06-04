/**
 * 
 */
package com.kishore.learn.rsocket.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;

/**
 * @author kishore
 *
 */
@Controller
public class SocketClientHandler {

	private static final Logger logger = LoggerFactory.getLogger(SocketClientHandler.class);

	private List<RSocketRequester> requesters = new ArrayList<>();

	@PreDestroy
	void shutdown() {
		this.requesters.stream().forEach( requester -> requester.rsocket().dispose());
		logger.info("Shutdown all the rsocket communications");
	}
	
	@ConnectMapping("shell-client")
	void connectShellClientForFreeMemory(RSocketRequester rSocketRequester, @Payload String client) {
		rSocketRequester.rsocket().onClose().doFirst(() -> {
			logger.info("Client connected ..!! {}", client);
			requesters.add(rSocketRequester);
		}).doOnError(error -> {
			logger.warn("Channel to client {} CLOSED", client); // (3)
		}).doFinally(consumer -> {
			requesters.remove(rSocketRequester);
			logger.info("Client {} DISCONNECTED", client); // (4)
		}).subscribe();
		rSocketRequester.route("client-status").data("OPEN").retrieveFlux(String.class)
				.doOnNext(s -> logger.info("Client: {} Free Memory: {}.", client, s)).subscribe();
	}
}
