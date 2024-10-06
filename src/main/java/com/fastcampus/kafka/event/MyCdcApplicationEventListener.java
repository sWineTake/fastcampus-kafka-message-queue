package com.fastcampus.kafka.event;

import com.fastcampus.kafka.model.MyCdcMessage;
import com.fastcampus.kafka.model.MyModelConverter;
import com.fastcampus.kafka.producer.MyCdcProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MyCdcApplicationEventListener {

	private final MyCdcProducer myCdcProducer;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void event(MyCdcApplicationEvent parameter) {
		MyCdcMessage message =
			MyModelConverter.toMessage(
				parameter.getId(),
				parameter.getMyModel(),
				parameter.getOperationType()
			);

		myCdcProducer.sendMessage(message);
	}


}
