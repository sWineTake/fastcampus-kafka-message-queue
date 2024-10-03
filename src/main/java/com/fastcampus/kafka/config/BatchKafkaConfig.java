package com.fastcampus.kafka.config;

import com.fastcampus.kafka.model.MyMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

// 배치 리스너 컨슈머 설정 -> 한번에 여러개의 메시지를 처리 하도록 수정
@Configuration
public class BatchKafkaConfig {

	@Bean
	@Qualifier("batchConsumerFactory")
	public ConsumerFactory<String, MyMessage> batchConsumerFactory(KafkaProperties kafkaProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getKeyDeserializer());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getValueDeserializer());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
		props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MyMessage.class.getName());
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "false");
		// 컨슈머가 한번에 최대 몇개의 레코드를 POLL할지 설정 : 기본값인 500개로 설정
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, ConsumerConfig.DEFAULT_MAX_POLL_RECORDS);
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	@Qualifier("batchKafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, MyMessage> batchKafkaListenerContainerFactory(
		ConsumerFactory<String, MyMessage> batchConsumerFactory
	) {
		ConcurrentKafkaListenerContainerFactory<String, MyMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(batchConsumerFactory);

		// 배치 컨슈머를 사용으로 변경
		factory.setBatchListener(true);
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

		// 리스너 컨테이너의 동시성을 설정 - 하나의 스레드만 사용하여 메시지를 처리
		factory.setConcurrency(1);
		return factory;
	}

}
