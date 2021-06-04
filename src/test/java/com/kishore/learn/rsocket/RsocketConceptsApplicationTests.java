package com.kishore.learn.rsocket;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.rsocket.context.LocalRSocketServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import com.kishore.learn.rsocket.mapping.RequestResponseMapping;
import com.kishore.rsocket.data.Message;

import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.WellKnownMimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RsocketConceptsApplicationTests {

	private static RSocketRequester requester;
	private static UsernamePasswordMetadata credentials;
	private static MimeType mimeType;
	
	@BeforeAll
	public static void setupOnce(
			@Autowired RSocketRequester.Builder builder,
			@LocalRSocketServerPort Integer localServerPort,
			@Autowired RSocketStrategies rSocketStrategies
			) {
//		requester = builder.tcp("localhost", localServerPort);
		SocketAcceptor responder = RSocketMessageHandler.responder(rSocketStrategies, new RequestResponseMapping());
		credentials = new UsernamePasswordMetadata("kishore", "password123");
		mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());
		
		requester = builder
				.setupRoute("shell-client")
				.setupData(UUID.randomUUID().toString())
				.setupMetadata(credentials, mimeType)
				.rsocketStrategies(strategy -> strategy.encoder(new SimpleAuthenticationEncoder()))
				.rsocketConnector(connector -> connector.acceptor(responder))
				.connectTcp("localhost", localServerPort)
				.block();
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
	@AfterAll
	void tearDownAllConnections() {
		requester.rsocket().dispose();
	}

}
