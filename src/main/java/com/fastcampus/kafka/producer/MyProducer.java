package com.fastcampus.kafka.producer;

import com.fastcampus.kafka.model.MyMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.fastcampus.kafka.model.Topic.MY_JSON_TOPIC;

@Component
@RequiredArgsConstructor
public class MyProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(MyMessage myMessage) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {

			kafkaTemplate.send(
				MY_JSON_TOPIC,
				myMessage.getName(),
				objectMapper.writeValueAsString(myMessage));

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

}
