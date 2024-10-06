package com.fastcampus.kafka.domain;

import com.fastcampus.kafka.model.MyModel;
import com.fastcampus.kafka.service.MyServiceImpl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MyController {

	private final MyServiceImpl myServiceImpl;

	@PostMapping("/gree")
	MyModel create(@RequestBody Request request) {
		if (request == null || request.getUserId() == null || request.getUserAge()  == null || request.getContent() == null) {
			return null;
		}

		MyModel create = MyModel.Create(
			request.getUserId(),
			request.getUserAge(),
			request.getUserName(),
			request.getContent()
		);

		return myServiceImpl.saveEntity(create);
	}

	@GetMapping("/gree")
	List<MyModel> getList() {
		return myServiceImpl.findAll();
	}

	@GetMapping("/gree/{id}")
	MyModel getDetail(@PathVariable Integer id) {
		return myServiceImpl.findById(id);
	}

	@DeleteMapping("/gree/{id}")
	void deleteDetail(@PathVariable Integer id) {
		myServiceImpl.deleteEntity(id);
	}

	@Data
	private static class Request {
		Integer userId;
		String userName;
		Integer userAge;
		String content;
	}


}
