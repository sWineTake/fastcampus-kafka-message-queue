package com.fastcampus.kafkahandson.data.outbox;


import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventsRepository extends JpaRepository<OutboxEvents, Long> {



}
