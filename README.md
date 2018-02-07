A simple toy project to experiment on spring amqp instrumentation

(requires snapshot build of brave spring-amqp)

./gradlew build docker

docker-compose up --scale spring-amqp-consumer=2