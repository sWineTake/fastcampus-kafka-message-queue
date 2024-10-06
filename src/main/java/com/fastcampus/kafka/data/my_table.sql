CREATE TABLE my_table
(
	id int PRIMARY KEY AUTO_INCREMENT COMMENT '컨텐츠 ID',
	user_id int,
	user_age int,
	user_name varchar(200),
	content text,
	create_at timestamp default CURRENT_TIMESTAMP,
	updated_at timestamp default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
