package com.kishore.learn.rsocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class RsocketConceptsApplication {

	private static final Logger logger = LogManager.getLogger(RsocketConceptsApplication.class);
	
	public static void main(String[] args) {
		logger.info("About to start server...");
		SpringApplication.run(RsocketConceptsApplication.class, args);
	}

//	@MessageMapping("request-response")
//	public Mono<Message> requestResponse(Message msg){
//		logger.info("Received request -message : ", msg);
//		return Mono.just(new Message("You have said.."+ msg.getMessage()));
//	}
}
