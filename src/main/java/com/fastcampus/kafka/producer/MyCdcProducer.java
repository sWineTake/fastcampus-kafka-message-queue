package com.fastcampus.kafka.producer;

import com.fastcampus.kafka.common.CustomObjectMapper;
import com.fastcampus.kafka.model.MyCdcMessage;
import com.fastcampus.kafka.model.MyMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.fastcampus.kafka.model.Topic.MY_CDC_TOPIC;
import static com.fastcampus.kafka.model.Topic.MY_JSON_TOPIC;

@Component
@RequiredArgsConstructor
public class MyCdcProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public void sendMessage(MyCdcMessage myCdcMessage) {
		ObjectMapper objectMapper = new CustomObjectMapper();
		try {

			kafkaTemplate.send(
				MY_CDC_TOPIC,
				String.valueOf(myCdcMessage.getId()),
				objectMapper.writeValueAsString(myCdcMessage)
			);

		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

}
