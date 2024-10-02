package com.fastcampus.kafka.domain;

import com.fastcampus.kafka.model.MyMessage;
import com.fastcampus.kafka.producer.MyProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {

	private final MyProducer myProducer;

	@PostMapping("/message")
	void message(@RequestBody MyMessage myMessage) {
		myProducer.sendMessage(myMessage);
	}

}
