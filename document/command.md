### 토픽 생성
`kafka-topics.sh --create --topic <토픽명> --bootstrap-server localhost:<포트> --replication-factor <복제갯수> --partitions <파티션갯수>`

## 토픽 리스트 확인
`kafka-topics.sh --list --bootstrap-server localhost:<포트>`

## 토픽 삭제
`kafka-topics.sh --delete --topic <토픽명> --bootstrap-server localhost:<포트>`

### 토픽 수정
`kafka-topics.sh --topic <토픽명> --bootstrap-server localhost:포트 --alter --partitions <수정 파티션수>`

### 컨슈머 연결
`kafka-console-consumer.sh --bootstrap-server localhost:<포트> --topic <토픽명> --from-beginning`
- `--partition <파티션 번호>` : 특정 파티션에 만 연결하여 볼수있음

### 프로듀서 연결
`kafka-console-producer.sh --bootstrap-server localhost:<포트> --topic <토픽명>`
- `--property="parse.key=true" --property="key.separator=:"` : 키를 설정하고 키의 구분자는 `:` 이다.


### Kcat
`docker run -it --rm --name=kcat --network=fastcampus-kafka-message-queue_default edenhill/kcat:1.7.1 -b kafka1:19092,kafka2:19092,kafka3:19092 <모드옵션> <상세옵션>`
- 모드옵션
    - `-L` : 리스트옵션
