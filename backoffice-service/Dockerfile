# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-noble AS builder
WORKDIR /opt/app/builder
RUN ${JAVA_HOME}/bin/jlink \
  --add-modules java.se,jdk.management \
  --strip-debug --no-man-pages --no-header-files --compress=2 --output jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

FROM ubuntu:noble AS app
WORKDIR /opt/app
RUN <<EOF
  apt-get update
  apt-get install --yes --no-install-recommends curl
  rm --recursive --force /var/lib/apt/lists/*
EOF
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=builder /opt/app/builder/jre ${JAVA_HOME}
COPY --from=builder /opt/app/builder/extracted/dependencies/ ./
COPY --from=builder /opt/app/builder/extracted/spring-boot-loader/ ./
COPY --from=builder /opt/app/builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /opt/app/builder/extracted/application/ ./
RUN groupadd --system app && useradd --no-log-init --system --gid app app
USER app
ENTRYPOINT ["java", "-jar", "app.jar"]
ENV SERVICE_PORT=8080
HEALTHCHECK --start-period=30s --start-interval=5s --interval=30s --timeout=3s --retries=3 \
  CMD curl --fail --no-progress-meter http://localhost:${SERVICE_PORT}/actuator/health/liveness || exit 1
EXPOSE ${SERVICE_PORT}
