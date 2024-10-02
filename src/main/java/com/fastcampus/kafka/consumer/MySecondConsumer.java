package com.fastcampus.kafka.consumer;

import com.fastcampus.kafka.model.MyMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.fastcampus.kafka.model.Topic.MY_SECOND_TOPIC;

@Component
public class MySecondConsumer {

	@KafkaListener(
		topics = {MY_SECOND_TOPIC},
		groupId = "test-consumer-group",
		containerFactory = "secondKafkaListenerContainerFactory"
	)
	public void accept(ConsumerRecord<String, String> message) {
		// Todo) 메시지에서 우리가 정의한 메시지타입으로 받게 설정
		System.out.println("[SECOND] ****************************");
		System.out.println(message.value());
		System.out.println(message.offset());
		System.out.println(message.partition());
	}
}
