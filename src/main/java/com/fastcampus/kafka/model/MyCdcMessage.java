package com.fastcampus.kafka.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyCdcMessage {

	private Integer id;
	private Payload payload;
	private OperationType operationType;

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Payload {
		private Integer id;
		private Integer userId;
		private Integer userAge;
		private String name;
		private String content;
		private LocalDateTime createAt;
		private LocalDateTime updatedAt;
	}

}
