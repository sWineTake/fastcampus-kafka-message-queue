package com.fastcampus.kafkahandson.data.outbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvents {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Comment("일련번호")
	private Long id;

	@Column(name = "aggregate_type", nullable = false)
	@Comment("이벤트가 속한 집계의 유형을 나타냅니다.")
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false)
	@Comment("특정 집계 인스턴스의 식별자입니다.")
	private String aggregateId;

	@Column(name = "event_type", nullable = false)
	@Comment("발생한 이벤트의 유형을 나타냅니다.")
	@Enumerated(EnumType.STRING)
	private EventType eventType;

	@Column(name = "payload")
	@Comment("STRING 형식으로 이벤트 데이터를 저장합니다.")
	private String payload;

	@Column(name = "created_at")
	@Comment("이벤트가 발생한 시각을 나타냅니다.")
	@CreationTimestamp
	private LocalDateTime createdAt;

	public static OutboxEvents create(String aggregateType, String aggregateId, EventType eventType, String payload) {
		OutboxEvents outboxEvents = new OutboxEvents();
		outboxEvents.aggregateType = aggregateType;
		outboxEvents.aggregateId = aggregateId;
		outboxEvents.eventType = eventType;
		outboxEvents.payload = payload;
		return outboxEvents;
	}

}
