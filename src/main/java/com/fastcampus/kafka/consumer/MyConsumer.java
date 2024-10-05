package com.fastcampus.kafka.consumer;

import com.fastcampus.kafka.model.MyMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.jms.AcknowledgeMode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fastcampus.kafka.model.Topic.MY_JSON_TOPIC;

@Component
public class MyConsumer {

	private final Map<String, Integer> idHistoryMap = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper = new ObjectMapper();
	@KafkaListener(
		topics = {MY_JSON_TOPIC},
		groupId = "test-consumer-group",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void accept(ConsumerRecord<String, String> message, Acknowledgment acknowledgment) throws JsonProcessingException {
		// Todo) 메시지에서 우리가 정의한 메시지타입으로 받게 설정
		MyMessage myMessage = objectMapper.readValue(message.value(), MyMessage.class);
		this.printPayloadIfFirstMessage(myMessage);

		// 수동으로 커밋
		acknowledgment.acknowledge();
	}

	private void printPayloadIfFirstMessage(MyMessage message) {
		if (idHistoryMap.putIfAbsent(String.valueOf(message.getId()), 1) == null) {
			System.out.println("[Exactly One RUN]****************************");
			System.out.println(message);
		} else {
			System.out.println("[Duplicate Data]****************************");
			System.out.println(message);
		}
	}

	@KafkaListener(
		topics = {MY_JSON_TOPIC},
		groupId = "batch-test-consumer-group",
		containerFactory = "batchKafkaListenerContainerFactory"
	)
	public void accept(List<ConsumerRecord<String, String>> messages) {
		/*ObjectMapper objectMapper = new ObjectMapper();
		System.out.println("[BATCH]****************************");
		messages.forEach(msg -> {
			MyMessage myMessage;
			try {
				myMessage = objectMapper.readValue(msg.value(), MyMessage.class);
				System.out.println(myMessage.toString());
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		});*/
	}
}
