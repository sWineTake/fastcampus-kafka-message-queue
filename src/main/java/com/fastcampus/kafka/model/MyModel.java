package com.fastcampus.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyModel {
	private final Integer id;
	private final Integer userId;
	private final Integer userAge;
	private final String username;
	private String content;
	private final LocalDateTime createAt;
	private final LocalDateTime updatedAt;

	public static MyModel Create(
		Integer userId,
		Integer userAge,
		String username,
		String content
	) {
		return new MyModel(null, userId, userAge, username, content, null, null);
	}

}
