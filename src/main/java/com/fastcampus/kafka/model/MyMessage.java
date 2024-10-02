package com.fastcampus.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyMessage {

	private int id;
	private int age;
	private String name;
	private String Content;

}
