package com.fastcampus.kafka.domain;

import com.fastcampus.kafka.model.MyMessage;
import com.fastcampus.kafka.model.MySecondMessage;
import com.fastcampus.kafka.producer.MyProducer;
import com.fastcampus.kafka.producer.MySecondProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MyController {

	private final MyProducer myProducer;
	private final MySecondProducer mySecondProducer;

	@PostMapping("/message")
	void message(@RequestBody MyMessage myMessage) {
		myProducer.sendMessage(myMessage);
	}

	@PostMapping("/second/message")
	void secondMessage(@RequestBody MySecondMessage myMessage) {
		mySecondProducer.sendMessageWithKey(myMessage.getKey(), myMessage.getMessage());
	}

}
