package com.fastcampus.kafkahandson.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.embedded.Connect;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.debezium.engine.format.Json;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

import static com.fastcampus.kafkahandson.model.Topic.MY_CDC_TOPIC;
import static com.fastcampus.kafkahandson.model.Topic.MY_CUSTOM_CDC_TOPIC_DLT;

@Configuration
@RequiredArgsConstructor
public class DebeziumConfig {

	private final ObjectMapper objectMapper = new ObjectMapper();
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Bean
	public io.debezium.config.Configuration configuration() {
		return io.debezium.config.Configuration.create()
			.with("name", "outbox-connector")
			.with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
			.with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
			.with("offset.storage.file.filename", "/tmp/offsets.dat")
			.with("offset.flush.interval.ms", "60000")
			.with("database.hostname", "localhost")
			.with("database.port", "3306")
			.with("database.user", "myuser")
			.with("database.password", "mypassword")
			.with("database.dbname", "campus")
			.with("database.include.list", "campus")
			.with("table.include.list", "campus.outbox_events")
			.with("database.server.id", "184054")
			.with("database.server.name", "campus")
			.with("schema.history.internal.kafka.bootstrap.servers", "localhost:9092")
			.with("schema.history.internal.kafka.topic", "schema-changes.campus")
			.with("driver.class", "com.mysql.cj.jdbc.Driver")
			.with("database.serverTimezone", "Asia/Seoul")
			.with("topic.prefix", "debezium")
			.build();
	}

	@Bean
	public DebeziumEngine<ChangeEvent<String, String>> debeziumEngine(io.debezium.config.Configuration connector) {
		return DebeziumEngine.create(Json.class)
			.using(connector.asProperties())
			.notifying((records, committer) -> {
				for (ChangeEvent<String, String> record : records) {
					// 여기에 필요한 로직을 추가하세요
					kafkaTemplate.send(MY_CDC_TOPIC, record.key(), record.value());
				}
			}).build();
	}
}
