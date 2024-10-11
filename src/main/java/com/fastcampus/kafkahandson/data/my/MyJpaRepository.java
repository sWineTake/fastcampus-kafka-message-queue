package com.fastcampus.kafkahandson.data.my;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyJpaRepository extends JpaRepository<MyEntity, Integer> { }
