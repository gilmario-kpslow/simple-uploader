FROM eclipse-temurin:25-jdk-alpine AS compile
COPY . /app
WORKDIR /app
RUN --mount=type=cache,id=m2-cache,sharing=shared,target=/root/.m2 ./mvnw clean package -DskipTests

FROM registry.redhat.io/ubi9/openjdk-21-runtime
ENV LANG='pt_BR.UTF-8' LANGUAGE='pt_BR:pt'
COPY --from=compile /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=compile /app/target/quarkus-app/*.jar /deployments/
COPY --from=compile /app/target/quarkus-app/app/ /deployments/app/
COPY --from=compile /app/target/quarkus-app/quarkus/ /deployments/quarkus/
EXPOSE 8080
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
ENTRYPOINT ["bash" ,"-c","java $JAVA_OPTS -XX:+UseZGC -XX:+ZGenerational -Dspring.threads.virtual.enabled=true -jar $JAVA_APP_JAR"]
