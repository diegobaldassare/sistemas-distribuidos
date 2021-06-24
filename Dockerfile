FROM openjdk:latest

WORKDIR /usr/src/app

COPY build/libs/distribuidos-0.1.0.jar /app.jar

EXPOSE $PORT

CMD java -cp /app.jar Main