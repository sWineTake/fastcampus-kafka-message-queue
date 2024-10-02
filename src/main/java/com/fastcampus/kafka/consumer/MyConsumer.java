package com.fastcampus.kafka.consumer;

import com.fastcampus.kafka.model.MyMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.fastcampus.kafka.model.Topic.MY_JSON_TOPIC;

@Component
public class MyConsumer {

	@KafkaListener(
		topics = {MY_JSON_TOPIC},
		groupId = "test-consumer-group",
		containerFactory = "kafkaListenerContainerFactory"
	)
	public void accept(ConsumerRecord<String, MyMessage> message) {
		// Todo) 메시지에서 우리가 정의한 메시지타입으로 받게 설정
		System.out.println("[PRIMARY]****************************");
		System.out.println(message.value().toString());
	}
}
