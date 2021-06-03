package com.kishore.learn.rsocket;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;

import com.kishore.rsocket.data.Message;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RsocketConceptsApplicationTests {

	private static RSocketRequester requester;
	
	@BeforeAll
	public static void setupOnce(
			@Autowired RSocketRequester.Builder builder,
			@LocalRSocketServerPort Integer localServerPort,
			@Autowired RSocketStrategies rSocketStrategies
			) {
		requester = builder.tcp("localhost", localServerPort);
	}

	@Test
	void testRequestGetResponse() {
		Mono<Message> resMessage = requester.route("request-response")
				.data(new Message("You dam right ..!"))
				.retrieveMono(Message.class);
		StepVerifier.create(resMessage)
		.consumeNextWith(message -> {
			assertThat(message.getMessage()).isEqualTo("You have said..You dam right ..!");
		}).verifyComplete();
	}
	
	@Test
	void testFireAndForget() {
		Mono<Void> response = requester.route("fire-and-forget")
				.data(new Message("Hello"))
				.retrieveMono(Void.class);
		StepVerifier.create(response).verifyComplete();
	}
	
	@Test
	void testStreamOfMessages() {
		Flux<Message> messages = requester.route("request-stream")
				.data(new Message("Hello"))
				.retrieveFlux(Message.class);
		
		StepVerifier.create(messages)
		.consumeNextWith(message -> {
			assertThat(message.getMessage()).isEqualTo("Message interval 0");
		})
		.expectNextCount(0)
		.consumeNextWith(message -> {
			assertThat(message.getMessage()).isEqualTo("Message interval 1");
		})
		.thenCancel()
		.verify();
	}

}
