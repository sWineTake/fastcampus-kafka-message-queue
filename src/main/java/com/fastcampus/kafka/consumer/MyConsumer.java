package com.fastcampus.kafka.consumer;

import com.fastcampus.kafka.model.MyMessage;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class MyConsumer implements Consumer<Message<MyMessage>> {

	public MyConsumer() {
		System.out.println("MyConsumer - success");
	}

	@Override
	public void accept(Message<MyMessage> myMessageMessage) {
		// Todo) 메시지에서 우리가 정의한 메시지타입으로 받게 설정
		System.out.println("****************************");
		System.out.println(myMessageMessage.getPayload());
	}
}
