package com.fastcampus.kafkahandson.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

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
			.with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
			// .with("database.history.file.filename", "/tmp/dbhistory.dat")
			.with("database.history.file.filename", "./dbhistory.dat")
			.with("driver.class", "com.mysql.cj.jdbc.Driver")
			.with("database.serverTimezone", "Asia/Seoul")
			.build();
	}

	@Bean
	public DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine() {
		return DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
			.using(configuration().asProperties())
			.notifying(record -> {
				SourceRecord sourceRecord = record.record();
				String cloudEventJson = (String) sourceRecord.value();
				try {
					JsonNode cloudEvent = objectMapper.readTree(cloudEventJson);
					JsonNode data = cloudEvent.get("data");

					String aggregateType = data.get("aggregateType").asText().toLowerCase();
					String aggregateId = data.get("aggregateId").asText();
					String payload = data.get("payload").toString();

					String topic = aggregateType + "-events";
					System.out.println("**************************************************************");
					System.out.println("**************************************************************");
					System.out.println("**************************************************************");
					System.out.println("Sending to topic: " + topic);

					// kafkaTemplate.send(topic, aggregateId, payload);
				} catch (IOException e) {
					// 에러 처리
					e.printStackTrace();
				}
			}).build();
	}
}
