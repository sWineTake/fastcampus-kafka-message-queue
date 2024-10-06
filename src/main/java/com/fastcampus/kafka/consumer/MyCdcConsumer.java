package com.fastcampus.kafka.consumer;

import com.fastcampus.kafka.model.MyCdcMessage;
import com.fastcampus.kafka.model.MyMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.fastcampus.kafka.model.Topic.MY_CDC_TOPIC;
import static com.fastcampus.kafka.model.Topic.MY_JSON_TOPIC;

@Component
public class MyCdcConsumer {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@KafkaListener(
		topics = {MY_CDC_TOPIC},
		groupId = "cdc-consumer-group",
		containerFactory = "kafkaListenerContainerFactory",
		concurrency = "3"
	)
	public void accept(ConsumerRecord<String, String> message, Acknowledgment acknowledgment) throws JsonProcessingException {
		// Todo) 메시지에서 우리가 정의한 메시지타입으로 받게 설정
		MyCdcMessage myCdcMessage = objectMapper.readValue(message.value(), MyCdcMessage.class);

		System.out.println("[CDC]****************************");
		System.out.println(myCdcMessage.getPayload().toString());

		// 수동으로 커밋
		acknowledgment.acknowledge();
	}

}
