package com.fastcampus.kafka.service;

import com.fastcampus.kafka.data.MyJpaRepository;
import com.fastcampus.kafka.data.MyTable;
import com.fastcampus.kafka.event.MyCdcApplicationEvent;
import com.fastcampus.kafka.model.MyModel;
import com.fastcampus.kafka.model.MyModelConverter;
import com.fastcampus.kafka.model.OperationType;
import com.fastcampus.kafka.producer.MyCdcProducer;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.core.ApplicationPushBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyServiceImpl implements MyService{

	private final MyJpaRepository myJpaRepository;
	private final MyCdcProducer myCdcProducer;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public List<MyModel> findAll() {
		List<MyTable> all = myJpaRepository.findAll();
		return all.stream().map(MyModelConverter::toModel).toList();
	}

	@Override
	public MyModel findById(Integer id) {
		Optional<MyTable> byId = myJpaRepository.findById(id);
		return byId.map(MyModelConverter::toModel).orElse(null);
	}

	@Override
	@Transactional
	public MyModel save(MyModel model) {
		MyTable save = myJpaRepository.save(MyModelConverter.toEntity(model));

		try {
			OperationType operationType =
				model.getId() == null ? OperationType.CREATE : OperationType.UPDATE;

			myCdcProducer.sendMessage(
				MyModelConverter.toMessage(
					save.getId(),
					MyModelConverter.toModel(save),
					operationType
				)
			);
		} catch (Exception e) {
			throw new RuntimeException("ERROR -");
		}

		return MyModelConverter.toModel(save);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		myJpaRepository.deleteById(id);
	}

	/// event를 사용하여 처리
	@Override
	@Transactional
	public MyModel saveEvent(MyModel model) {
		MyTable save = myJpaRepository.save(MyModelConverter.toEntity(model));

		OperationType operationType =
			model.getId() == null ? OperationType.CREATE : OperationType.UPDATE;
		eventPublisher.publishEvent(
			new MyCdcApplicationEvent(
				this,
				save.getId(),
				MyModelConverter.toModel(save),
				operationType
			)
		);

		return MyModelConverter.toModel(save);
	}

	@Override
	@Transactional
	public void deleteEvent(Integer id) {
		myJpaRepository.deleteById(id);

		eventPublisher.publishEvent(
			new MyCdcApplicationEvent(
				this,
				id,
				null,
				OperationType.DELETE
			)
		);
	}

	// entity Listener
	@Override
	@Transactional
	public MyModel saveEntity(MyModel model) {
		MyTable save = myJpaRepository.save(MyModelConverter.toEntity(model));
		return MyModelConverter.toModel(save);
	}

	@Override
	@Transactional
	public void deleteEntity(Integer id) {
		myJpaRepository.deleteById(id);
	}

}
