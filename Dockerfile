
FROM openjdk:17-jdk-slim-buster

ENV DEBIAN_FRONTEND=noninteractive
# basic smoke test

WORKDIR "/"
RUN java --version

RUN apt-get update
RUN apt-get install bash
RUN apt-get install -y curl

RUN mkdir shell
RUN mkdir www
RUN mkdir web
RUN mkdir js
RUN mkdir target
RUN mkdir -p sql/create
RUN mkdir logs
RUN mkdir SampleBids
RUN mkdir Campaigns
run mkdir query

COPY target/RTB5-0.0.1-SNAPSHOT-jar-with-dependencies.jar target/

COPY wait-for-it.sh /
RUN chmod +x /wait-for-it.sh

COPY tools/* /
COPY sql/create/* sql/create/
COPY shell/ /shell

COPY query/ query/

COPY www/index.html /www
COPY www/css/ css/
COPY www/fonts/ fonts/
COPY www/assets/ assets/
copy www/campaigns campaigns/

COPY log4j2.properties /
COPY SampleBids /SampleBids

EXPOSE 8080 5701

CMD ./rtb4free
