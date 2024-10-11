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

