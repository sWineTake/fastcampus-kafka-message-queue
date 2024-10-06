package com.fastcampus.kafka.event;

import com.fastcampus.kafka.model.MyModel;
import com.fastcampus.kafka.model.OperationType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MyCdcApplicationEvent extends ApplicationEvent {

	private final Integer id;
	private final MyModel myModel;
	private final OperationType operationType;

	public MyCdcApplicationEvent(Object source, Integer id, MyModel myModel, OperationType operationType) {
		super(source);
		this.id = id;
		this.myModel = myModel;
		this.operationType = operationType;
	}

}
