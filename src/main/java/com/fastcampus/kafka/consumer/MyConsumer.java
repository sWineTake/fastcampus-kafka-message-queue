package com.fastcampus.kafka.consumer;

import com.fastcampus.kafka.model.MyMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MyConsumer {

	@KafkaListener(
		topics = {"my-json-topic"},
		groupId = "test-consumer-group"
	)
	public void accept(ConsumerRecord<String, MyMessage> message) {
		// Todo) 메시지에서 우리가 정의한 메시지타입으로 받게 설정
		System.out.println("****************************");
		System.out.println(message.value().toString());
	}
}
