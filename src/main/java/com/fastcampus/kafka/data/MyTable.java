package com.fastcampus.kafka.data;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity(name = "my_table")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(value = MyEntityListener.class) // 리스너 등록
public class MyTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Integer userId;

	private Integer userAge;

	@Column(name = "user_name")
	private String username;

	private String content;
	@CreationTimestamp
	private LocalDateTime createAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public MyTable(Integer userId, Integer userAge, String username, String content) {
		this.userId = userId;
		this.userAge = userAge;
		this.username = username;
		this.content = content;
	}
}
