package com.fastcampus.kafka.data;

import com.fastcampus.kafka.model.MyCdcMessage;
import com.fastcampus.kafka.model.MyModelConverter;
import com.fastcampus.kafka.model.OperationType;
import com.fastcampus.kafka.producer.MyCdcProducer;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MyEntityListener {

	private final MyCdcProducer myCdcProducer;

	@PostPersist
	public void handleCreate(MyTable myTable) {
		MyCdcMessage message;
		try {
			message =
				MyModelConverter.toMessage(
					myTable.getId(),
					MyModelConverter.toModel(myTable),
					OperationType.CREATE
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		myCdcProducer.sendMessage(message);
	}

	@PostUpdate
	public void handleUpdate(MyTable myTable) {
		MyCdcMessage message;
		try {
			message =
				MyModelConverter.toMessage(
					myTable.getId(),
					MyModelConverter.toModel(myTable),
					OperationType.UPDATE
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		myCdcProducer.sendMessage(message);
	}

	@PostRemove
	public void handleDelete(MyTable myTable) {
		MyCdcMessage message;
		try {
			message =
				MyModelConverter.toMessage(
					myTable.getId(),
					null,
					OperationType.DELETE
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		myCdcProducer.sendMessage(message);
	}

}
