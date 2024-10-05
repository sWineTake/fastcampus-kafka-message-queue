package com.fastcampus.kafka.config;

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

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

	@Bean
	@Primary
	@ConfigurationProperties("spring.kafka")
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
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "false");

		// 수동 커밋으로 변경
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
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
		// 수동 커밋으로 변경
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
		return factory;
	}

	@Bean
	@Qualifier("batchConsumerFactory")
	public ConsumerFactory<String, String> batchConsumerFactory(KafkaProperties kafkaProperties) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getKeyDeserializer());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getValueDeserializer());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
		props.put(ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG, "false");
		// 컨슈머가 한번에 최대 몇개의 레코드를 POLL할지 설정 : 기본값인 500개로 설정
		props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, ConsumerConfig.DEFAULT_MAX_POLL_RECORDS);
		return new DefaultKafkaConsumerFactory<>(props);
	}

	@Bean
	@Qualifier("batchKafkaListenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, String> batchKafkaListenerContainerFactory(
		@Qualifier("batchConsumerFactory") ConsumerFactory<String, String> batchConsumerFactory
	) {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(batchConsumerFactory);

		// 배치 컨슈머를 사용으로 변경
		factory.setBatchListener(true);
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);

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
		// 모든 메시지 전파 확인
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		// EOS 설정 (메시지의 고유 키값을 보냄 : Producer ID + 시퀀스 넘버 = 유니크 키) 파티션 중복 메시지를 확인 할 수 있음
		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");

		// 'retries' 설정: 일시적인 오류 발생 시 메시지 전송을 재시도할 횟수
		// 여기서는 3회로 설정, 네트워크 불안정 등으로 인한 일시적 오류를 극복하는 데 도움
		props.put(ProducerConfig.RETRIES_CONFIG, 3);

		// 'retry.backoff.ms' 설정: 재시도 사이의 대기 시간(밀리초)
		// 여기서는 1초(1000ms)로 설정, 연속적인 재시도로 인한 부하를 줄이고
		// 일시적인 네트워크 문제가 해결될 시간을 제공
		props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);

		return new DefaultKafkaProducerFactory<>(props);
	}

	@Bean
	@Primary
	public KafkaTemplate<String, ?> kafkaTemplate(KafkaProperties kafkaProperties) {
		return new KafkaTemplate<>(producerFactory(kafkaProperties));
	}
}
