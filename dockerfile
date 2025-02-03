FROM eclipse-temurin:21-jre

WORKDIR /app
EXPOSE 8091

COPY ./target/*.jar /app/aplication.jar
COPY ./dependencies-tree.md /app

CMD ["sh", "-c", "java ${JVM_MEMORY_ARGS} ${JVM_ARGS} -jar /app/aplication.jar"]
