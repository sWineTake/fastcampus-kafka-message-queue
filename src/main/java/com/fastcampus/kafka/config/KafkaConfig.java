package com.fastcampus.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Bean
	@Primary
	@ConfigurationProperties("spring.kafka.json")
	public KafkaProperties kafkaProperties() {
		return new KafkaProperties();
	}

	@Bean
	@Primary
	public ConsumerFactory<String, Object> consumerFactory(KafkaProperties kafkaProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getKeyDeserializer());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getValueDeserializer());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaProperties.getConsumer().getAutoOffsetReset());
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "false");
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	@Primary
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
		ConsumerFactory<String, Object> consumerFactory
	) {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);
		// 리스너 컨테이너의 동시성을 설정 - 하나의 스레드만 사용하여 메시지를 처리
		factory.setConcurrency(1);
		return factory;
	}

	@Bean
	@Primary
	public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getKeySerializer());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getValueSerializer());
		props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.getProducer().getAcks());
		return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	@Primary
	public KafkaTemplate<String, ?> kafkaTemplate(KafkaProperties kafkaProperties) {
		return new KafkaTemplate<>(producerFactory(kafkaProperties));
	}

}
