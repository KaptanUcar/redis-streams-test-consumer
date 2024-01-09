FROM amazoncorretto:17-alpine-jdk AS build
WORKDIR /home/temp/
COPY . .
# RUN ./mvnw clean package

FROM amazoncorretto:17-alpine
WORKDIR /home/app/
COPY --from=build /home/temp/target/application.jar .
ENTRYPOINT java -jar application.jar