package com.fastcampus.kafka.model;


import com.fastcampus.kafka.data.MyTable;

public class MyModelConverter {

	public static MyModel toModel(MyTable entity) {
		return new MyModel(
			entity.getId(),
			entity.getUserId(),
			entity.getUserAge(),
			entity.getUsername(),
			entity.getContent(),
			entity.getCreateAt(),
			entity.getUpdatedAt()
		);
	}

	public static MyTable toEntity(MyModel model) {
		return new MyTable(
			model.getUserId(),
			model.getUserAge(),
			model.getUserName(),
			model.getContent()
		);
	}

	public static MyModel toModel(MyCdcMessage message) {
		return new MyModel(
			message.getId(),
			message.getPayload().getUserId(),
			message.getPayload().getUserAge(),
			message.getPayload().getName(),
			message.getPayload().getContent(),
			message.getPayload().getCreateAt(),
			message.getPayload().getUpdatedAt()
		);
	}

	public static MyCdcMessage toMessage(Integer id, MyModel model, OperationType operationType) {
		MyCdcMessage.Payload payload = null;
		if (operationType == OperationType.CREATE || operationType == OperationType.UPDATE) { // C, U의 경우만 payload 존재
			payload = new MyCdcMessage.Payload(
				model.getId(),
				model.getUserId(),
				model.getUserAge(),
				model.getUserName(),
				model.getContent(),
				model.getCreateAt(),
				model.getUpdatedAt()
			);
		}
		return new MyCdcMessage(
			id,
			payload,
			operationType
		);
	}
}
