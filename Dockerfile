FROM maven:3.9.9-eclipse-temurin-17 as compile
COPY . /app
WORKDIR /app
RUN --mount=type=cache,id=m2-cache,sharing=shared,target=/root/.m2 mvn clean package -DskipTests

FROM openjdk:17
ENV LANG='pt_BR.UTF-8' LANGUAGE='pt_BR:pt'

COPY --from=compile /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=compile /app/target/quarkus-app/*.jar /deployments/
COPY --from=compile /app/target/quarkus-app/app/ /deployments/app/
COPY --from=compile /app/target/quarkus-app/quarkus/ /deployments/quarkus/
EXPOSE 8080
ENV AB_JOLOKIA_OFF=""
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"
CMD java -jar $JAVA_OPTS $JAVA_APP_JAR
