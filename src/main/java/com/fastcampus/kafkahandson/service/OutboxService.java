package com.fastcampus.kafkahandson.service;

import com.fastcampus.kafkahandson.data.outbox.EventType;
import com.fastcampus.kafkahandson.data.outbox.OutboxEvents;
import com.fastcampus.kafkahandson.data.outbox.OutboxEventsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class OutboxService {

	private final OutboxEventsRepository outboxEventsRepository;

	public void publishEvent(String aggregateClass, String aggregateId, EventType eventType, String payload) {
		outboxEventsRepository.save(OutboxEvents.create(aggregateClass, aggregateId, eventType, payload));
	}
}
