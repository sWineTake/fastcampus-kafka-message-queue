package com.fastcampus.kafka.service;

import com.fastcampus.kafka.model.MyModel;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MyService {

	public List<MyModel> findAll();

	public MyModel findById(Integer id);

	public MyModel save(MyModel model);

	public void delete(Integer id);

	public void deleteEvent(Integer id);

	public MyModel saveEvent(MyModel model);

	public MyModel saveEntity(MyModel model);

	public void deleteEntity(Integer id);

}
