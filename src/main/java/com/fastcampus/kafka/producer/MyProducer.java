package com.fastcampus.kafka.producer;

import com.fastcampus.kafka.model.MyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.fastcampus.kafka.model.Topic.MY_JSON_TOPIC;

@Component
@RequiredArgsConstructor
public class MyProducer {

	private final KafkaTemplate<String, MyMessage> kafkaTemplate;

	public void sendMessage(MyMessage myMessage) {
		kafkaTemplate.send(MY_JSON_TOPIC, myMessage.getName(), myMessage);
	}

}
