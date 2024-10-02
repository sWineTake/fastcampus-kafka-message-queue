package com.fastcampus.kafka.producer;

import com.fastcampus.kafka.model.MyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyProducer {

	private final KafkaTemplate<String, MyMessage> kafkaTemplate;

	public void sendMessage(MyMessage myMessage) {
		kafkaTemplate.send("my-json-topic", myMessage.getName(), myMessage);
	}

}
