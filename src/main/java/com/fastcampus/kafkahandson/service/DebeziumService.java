package com.fastcampus.kafkahandson.service;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.connect.source.SourceRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class DebeziumService {
	private final DebeziumEngine<ChangeEvent<String, String>> debeziumEngine;

	@PostConstruct
	public void start() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(debeziumEngine);
	}

	@PreDestroy
	public void stop() throws IOException {
		if (debeziumEngine != null) {
			debeziumEngine.close();
		}
	}
}
