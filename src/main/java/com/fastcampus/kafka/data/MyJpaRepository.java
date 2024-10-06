package com.fastcampus.kafka.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyJpaRepository extends JpaRepository<MyTable, Integer> {



}
