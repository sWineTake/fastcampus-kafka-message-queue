create table campus.outbox_events
(
	id             bigint auto_increment
        primary key,
	aggregate_type varchar(255)                        not null,
	aggregate_id   varchar(255)                        not null,
	event_type     varchar(255)                        not null,
	payload        text                                not null,
	created_at     timestamp default CURRENT_TIMESTAMP null
)
	collate = utf8mb4_unicode_ci;

create index idx_outbox_events_aggregate
	on campus.outbox_events (aggregate_type, aggregate_id);

create index idx_outbox_events_created_at
	on campus.outbox_events (created_at);

-- Debezium 사용자에게 필요한 권한 부여
GRANT RELOAD, FLUSH_TABLES, SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'myuser'@'localhost';

-- 권한 변경 즉시 적용
FLUSH PRIVILEGES;
