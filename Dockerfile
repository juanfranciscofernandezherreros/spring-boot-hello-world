FROM eclipse-temurin:21-jre
VOLUME /tmp

ARG JAR_FILE=target/spring-boot-hello-world-0.0.1.jar
COPY ${JAR_FILE} /app.jar

ENV APP_ENV=dev
ENV JAR_OPTS=""
ENV JAVA_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $JAR_OPTS
