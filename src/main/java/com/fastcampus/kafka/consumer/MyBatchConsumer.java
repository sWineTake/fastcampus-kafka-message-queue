package com.fastcampus.kafka.consumer;

import com.fastcampus.kafka.model.MyMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.fastcampus.kafka.model.Topic.MY_JSON_TOPIC;

@Component
public class MyBatchConsumer {

	@KafkaListener(
		topics = {MY_JSON_TOPIC},
		groupId = "batch-test-consumer-group",
		containerFactory = "batchKafkaListenerContainerFactory"
	)
	public void accept(List<ConsumerRecord<String, MyMessage>> messages) {
		System.out.println("[BATCH]****************************");
		messages.forEach(msg -> {
			System.out.println(msg.value());
		});
	}
}


