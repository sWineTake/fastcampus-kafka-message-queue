package com.fastcampus.kafka.config;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.util.ErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.fastcampus.kafka.model.Topic.MY_CUSTOM_CDC_TOPIC_DLT;

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
	CommonErrorHandler errorHandler() {
		CommonContainerStoppingErrorHandler cseh = new CommonContainerStoppingErrorHandler();

		AtomicReference<Consumer<?, ?>> consumer2 = new AtomicReference<>();
		AtomicReference<MessageListenerContainer> container2 = new AtomicReference<>();

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(
			(rec, ex) -> {
				// 컨슈머 스탑핑 에러 핸들러를 통해서 해당 컨슈머를 중지시킨다.
				cseh.handleRemaining(ex, Collections.singletonList(rec), consumer2.get(), container2.get());

			},
			generatedBackOff()) {
			@Override
			public void handleRemaining(
				Exception thrownException,
				List<ConsumerRecord<?, ?>> records,
				Consumer<?, ?> consumer,
				MessageListenerContainer container
			) {

				consumer2.set(consumer);
				container2.set(container);
				super.handleRemaining(thrownException, records, consumer, container);
			}
		};

		errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

		return errorHandler;
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
		ConsumerFactory<String, Object> consumerFactory,
		KafkaTemplate<String, Object> kafkaTemplate
	) {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory);

		// 에러 헨들러 추가
		// DefaultErrorHandler errorHandler = new DefaultErrorHandler(exponetialBackOff());
		// errorHandler.addNotRetryableExceptions(IllegalArgumentException.class); // -> 해당 에러 발생시 재시도 하지않음 (예외처리)
		// factory.setCommonErrorHandler(errorHandler);

		// 에러 발생시 컨슈머가 멈추게 설정도 가능
		// factory.setCommonErrorHandler(new CommonContainerStoppingErrorHandler());

		// 커스텀에러 추가 : 에러핸들러 추가방식 - 재시도 후에 컨슈머 스탑핑 에러 핸들러발생하여 컨슈머 중지
		// factory.setCommonErrorHandler(errorHandler);

		// 데드 레더 큐 추가
		factory.setCommonErrorHandler(
			new DefaultErrorHandler(
					// 해당 정책 재발송으로 하여도 에러가 발생하면 - 데드레더 토픽에 프로듀서됨
					(recod, ex) -> {
						// recod : 실패한 메시지
						kafkaTemplate.send(MY_CUSTOM_CDC_TOPIC_DLT, (String) recod.key(), recod.value());
					}
				, generatedBackOff() // 해당 정책으로 재발송 실행
			)
		);

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
		// 수정 커밋으로 변경
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
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
		// 수동모드
		factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

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

	// 재시도 케이스 1번
	private BackOff generatedBackOff() {
		return new FixedBackOff(1000L, 3L); // 1초에 3번 재시도
	}

	// 재시도 케이스 2번
	private BackOff exponetialBackOff() {
		ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2L); // 1초 간격으로 시작해서 2배씩 간격이 증가
		// backOff.setMaxElapsedTime(10000L); // 최대 10초까지만 증가
		backOff.setMaxAttempts(1); // 최대 한번 실행
		return backOff;
	}
}
