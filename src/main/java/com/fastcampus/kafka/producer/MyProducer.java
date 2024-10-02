package com.fastcampus.kafka.producer;

import com.fastcampus.kafka.model.MyMessage;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Supplier;

@Component
public class MyProducer implements Supplier<Flux<Message<MyMessage>>> {

	// Sinks.Many를 사용하여 메시지를 비동기적으로 발행할 수 있는 싱크를 생성
	private final Sinks.Many<Message<MyMessage>> sinks = Sinks.many().unicast().onBackpressureBuffer();

	@Override
	public Flux<Message<MyMessage>> get() {
		return sinks.asFlux();
	}

	public void sendMessage(MyMessage myMessage) {
		// 메시지 생성
		Message<MyMessage> message = MessageBuilder
										.withPayload(myMessage)
										.setHeader(KafkaHeaders.KEY, myMessage.getName())
										.build();

		// (메시지 전송) Sinks.EmitFailureHandler.FAIL_FAST : 메시지 발행 실패 시 즉시 에러를 발생시키는 옵션
		sinks.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST);
	}

}
